package com.gym.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchitectureRulesTest {
    private static final Path MAIN_SOURCE_ROOT = Path.of("src/main/java");
    private static final Path GYM_SOURCE_ROOT = MAIN_SOURCE_ROOT.resolve("com/gym");

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^\\s*package\\s+([a-zA-Z_][\\w.]*)\\s*;");
    private static final Pattern IMPORT_PATTERN = Pattern.compile("^\\s*import\\s+(?:static\\s+)?([a-zA-Z_][\\w.]*|[a-zA-Z_][\\w.]*\\.\\*)\\s*;");
    private static final List<String> DOMAIN_FORBIDDEN_PREFIXES = List.of(
            "org.springframework",
            "jakarta.",
            "java.sql",
            "com.fasterxml.jackson"
    );
    private static final List<String> PERSISTENCE_JPA_PREFIXES = List.of(
            "jakarta.persistence",
            "org.springframework.data.jpa"
    );
    private static final List<String> WRITE_VIEW_SUFFIXES = List.of("Detail", "ListItem", "Summary");

    @Test
    void sourceCodeMustFollowHexagonalModuleRules() throws IOException {
        List<String> violations = new ArrayList<>();
        for (SourceFile sourceFile : readSourceFiles()) {
            collectR1ApplicationMustNotImportAdapter(sourceFile, violations);
            collectR2DomainMustStayFrameworkFree(sourceFile, violations);
            collectR3CrossModuleImportsMustGoThroughApi(sourceFile, violations);
            collectR4WriteSideMustNotDependOnReadViews(sourceFile, violations);
            collectR5PersistenceMustNotUseJpa(sourceFile, violations);
        }

        assertTrue(violations.isEmpty(), () -> "Architecture rule violations:\n" + String.join("\n", violations));
    }

    private static List<SourceFile> readSourceFiles() throws IOException {
        if (!Files.exists(GYM_SOURCE_ROOT)) {
            return List.of();
        }
        try (Stream<Path> paths = Files.walk(GYM_SOURCE_ROOT)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(ArchitectureRulesTest::readSourceFile)
                    .toList();
        }
    }

    private static SourceFile readSourceFile(Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            String packageName = lines.stream()
                    .map(PACKAGE_PATTERN::matcher)
                    .filter(Matcher::find)
                    .map(matcher -> matcher.group(1))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Missing package declaration: " + readablePath(path)));
            List<String> imports = lines.stream()
                    .map(IMPORT_PATTERN::matcher)
                    .filter(Matcher::find)
                    .map(matcher -> matcher.group(1))
                    .toList();
            return new SourceFile(path, packageName, imports);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read source file: " + readablePath(path), e);
        }
    }

    private static void collectR1ApplicationMustNotImportAdapter(SourceFile sourceFile, List<String> violations) {
        if (sourceFile.isBootstrapOrShared() || !"application".equals(sourceFile.layer())) {
            return;
        }
        for (String imported : sourceFile.imports()) {
            if (isGymLayerImport(imported, "adapter")) {
                violations.add(violation(sourceFile, "R1", imported, "application không được import adapter"));
            }
        }
    }

    private static void collectR2DomainMustStayFrameworkFree(SourceFile sourceFile, List<String> violations) {
        if (sourceFile.isBootstrapOrShared() || !"domain".equals(sourceFile.layer())) {
            return;
        }
        for (String imported : sourceFile.imports()) {
            if (startsWithAny(imported, DOMAIN_FORBIDDEN_PREFIXES)) {
                violations.add(violation(sourceFile, "R2", imported, "domain không được phụ thuộc framework hoặc java.sql"));
            }
            if (imported.startsWith("com.gym." + sourceFile.module() + ".application")
                    || imported.startsWith("com.gym." + sourceFile.module() + ".adapter")) {
                violations.add(violation(sourceFile, "R2", imported, "domain không được import application/adapter của chính module"));
            }
        }
    }

    private static void collectR3CrossModuleImportsMustGoThroughApi(SourceFile sourceFile, List<String> violations) {
        if (sourceFile.isBootstrapOrShared()) {
            return;
        }
        for (String imported : sourceFile.imports()) {
            Optional<GymImport> gymImport = GymImport.parse(imported);
            if (gymImport.isEmpty()) {
                continue;
            }
            GymImport target = gymImport.get();
            if (!target.module().equals(sourceFile.module())
                    && !"shared".equals(target.module())
                    && !"api".equals(target.layer())) {
                violations.add(violation(sourceFile, "R3", imported, "cross-module phải qua api"));
            }
        }
    }

    private static void collectR4WriteSideMustNotDependOnReadViews(SourceFile sourceFile, List<String> violations) {
        if (!isWriteSide(sourceFile.path())) {
            return;
        }
        for (String imported : sourceFile.imports()) {
            if (isReadViewImport(imported)) {
                violations.add(violation(sourceFile, "R4", imported, "write side không được nhận read view"));
            }
        }
    }

    private static void collectR5PersistenceMustNotUseJpa(SourceFile sourceFile, List<String> violations) {
        if (!sourceFile.packageName().contains(".adapter.out.persistence")) {
            return;
        }
        for (String imported : sourceFile.imports()) {
            if (startsWithAny(imported, PERSISTENCE_JPA_PREFIXES)) {
                violations.add(violation(sourceFile, "R5", imported, "persistence dùng Native SQL, không JPA"));
            }
        }
    }

    private static boolean isGymLayerImport(String imported, String forbiddenLayer) {
        return GymImport.parse(imported)
                .map(gymImport -> forbiddenLayer.equals(gymImport.layer()))
                .orElse(false);
    }

    private static boolean isWriteSide(Path path) {
        String fileName = path.getFileName().toString();
        return (fileName.startsWith("Write") && fileName.endsWith("Port.java"))
                || fileName.endsWith("WriteAdapter.java");
    }

    private static boolean isReadViewImport(String imported) {
        int viewPackageIndex = imported.indexOf(".application.view.");
        if (viewPackageIndex < 0) {
            return false;
        }
        if (imported.endsWith(".*")) {
            return true;
        }
        String simpleName = imported.substring(imported.lastIndexOf('.') + 1);
        return WRITE_VIEW_SUFFIXES.stream().anyMatch(simpleName::endsWith);
    }

    private static boolean startsWithAny(String value, List<String> prefixes) {
        return prefixes.stream().anyMatch(value::startsWith);
    }

    private static String violation(SourceFile sourceFile, String rule, String imported, String reason) {
        return "[" + rule + "] " + readablePath(sourceFile.path()) + " imports " + imported + " (" + reason + ")";
    }

    private static String readablePath(Path path) {
        return MAIN_SOURCE_ROOT.relativize(path).toString().replace('\\', '/');
    }

    private record SourceFile(Path path, String packageName, List<String> imports) {
        boolean isBootstrapOrShared() {
            return module() == null || "shared".equals(module());
        }

        String module() {
            if ("com.gym".equals(packageName)) {
                return null;
            }
            if (!packageName.startsWith("com.gym.")) {
                return null;
            }
            String rest = packageName.substring("com.gym.".length());
            int dot = rest.indexOf('.');
            return dot < 0 ? rest : rest.substring(0, dot);
        }

        String layer() {
            String module = module();
            if (module == null) {
                return null;
            }
            String prefix = "com.gym." + module + ".";
            if (!packageName.startsWith(prefix)) {
                return null;
            }
            String rest = packageName.substring(prefix.length());
            int dot = rest.indexOf('.');
            return dot < 0 ? rest : rest.substring(0, dot);
        }
    }

    private record GymImport(String module, String layer) {
        static Optional<GymImport> parse(String imported) {
            if (!imported.startsWith("com.gym.")) {
                return Optional.empty();
            }
            String[] parts = imported.split("\\.");
            if (parts.length < 4) {
                return Optional.empty();
            }
            return Optional.of(new GymImport(parts[2], parts[3]));
        }
    }
}
