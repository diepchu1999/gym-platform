#!/usr/bin/env bash
set -euo pipefail

# Chạy môi trường dev: bật hạ tầng (Postgres...) rồi chạy backend.
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "==> Starting infra (docker compose)"
docker compose -f "$ROOT/infra/docker/docker-compose.yml" up -d postgres

echo "==> Running backend (Spring Boot) on :8080"
cd "$ROOT/gym-platform-api"
./mvnw spring-boot:run
