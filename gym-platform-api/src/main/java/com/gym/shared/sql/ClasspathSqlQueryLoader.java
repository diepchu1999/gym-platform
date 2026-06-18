package com.gym.shared.sql;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reads SQL from the classpath ({@code src/main/resources}) and caches it by
 * resource path.
 *
 * <p>The cache lives inside this singleton bean instance - it is NOT static or
 * global mutable state. SQL files are read-only build artifacts, so caching them
 * permanently is safe and avoids re-reading from disk on every adapter query.
 * With devtools, each restart's classloader creates a new bean, so the cache
 * resets automatically.
 */
@Component
public class ClasspathSqlQueryLoader implements SqlLoader {

    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     *
     * @param resourcePath the classpath path to the SQL file
     * @return the (cached) SQL file contents
     * @throws IllegalArgumentException if the resource does not exist on the classpath
     */
    @Override
    public String load(String resourcePath) {
        return cache.computeIfAbsent(resourcePath, ClasspathSqlQueryLoader::read);
    }

    /**
     * Reads the SQL resource from the classpath as UTF-8 text.
     *
     * @param resourcePath the classpath path to the SQL file
     * @return the file contents
     * @throws IllegalArgumentException if the resource does not exist
     * @throws UncheckedIOException     if the resource cannot be read
     */
    private static String read(String resourcePath) {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        if (!resource.exists()) {
            throw new IllegalArgumentException(
                    "SQL resource not found on classpath: " + resourcePath);
        }
        try (InputStream in = resource.getInputStream()) {
            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Failed to read SQL resource: " + resourcePath, e);
        }
    }
}
