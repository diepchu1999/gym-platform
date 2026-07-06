# syntax=docker/dockerfile:1.7

# ----------------------------
# Build stage
# ----------------------------
# Build directly on ARM VM or via GitHub Actions buildx linux/arm64.
# Eclipse Temurin publishes multi-arch images, including arm64.
FROM eclipse-temurin:26-jdk AS build

WORKDIR /workspace

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw
RUN ./mvnw -q -B -DskipTests dependency:go-offline

COPY src src

RUN ./mvnw -q -B -DskipTests package
RUN java -Djarmode=tools -jar target/*.jar extract --layers --destination target/extracted


# ----------------------------
# Runtime stage
# ----------------------------
FROM eclipse-temurin:26-jre

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system gym \
    && useradd --system --gid gym --home-dir /app --shell /usr/sbin/nologin gym

COPY --from=build /workspace/target/extracted/dependencies/ ./
COPY --from=build /workspace/target/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/target/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/target/extracted/application/ ./

ENV SERVER_PORT=8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=65.0 -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=5 \
  CMD curl -fsS "http://127.0.0.1:${SERVER_PORT}/actuator/health" | grep -q '"status":"UP"' || exit 1

USER gym

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]