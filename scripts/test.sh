#!/usr/bin/env bash
set -euo pipefail

# Chạy test backend. Cần Postgres đang chạy (scripts/setup.sh hoặc docker compose up -d postgres).
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/gym-platform-api"
./mvnw test
