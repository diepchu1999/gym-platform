#!/usr/bin/env bash
set -euo pipefail

if [ ! -f .env ]; then
  cp .env.example .env
  echo "Created .env from .env.example"
fi

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
docker compose --env-file "$ROOT/.env" -f "$ROOT/infra/docker/docker-compose.yml" up -d

echo "Local services are running."
echo "PostgreSQL: localhost:5432"
echo "Redis: localhost:6379"
