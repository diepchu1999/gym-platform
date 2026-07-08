# UAT Deployment Configuration Guide

> This guide explains each configuration file needed to build, push, and run UAT. For copy-paste commands, read [`deployment-quick-runbook.md`](deployment-quick-runbook.md). For the overall Git/CI/CD workflow, read [`deployment-workflow.md`](deployment-workflow.md).

## 1. Goal

When setting up deployment for gym-platform or a similar Spring Boot project, answer four questions:

1. How is the app built?
2. Where is the image stored?
3. Which runtime services are needed?
4. Where are secrets and public URLs configured?

The files below answer those questions.

## 2. Deployment Files

| File | Status | Purpose |
|---|---|---|
| `gym-platform-api/Dockerfile` | Exists | Build the Spring Boot API image |
| `.github/workflows/ci.yml` | Exists | Run automated tests on push/PR |
| `.github/workflows/docker-image.yml` | Exists | Build/push Docker image to GHCR |
| `.github/workflows/cd.yml` | Missing | Target: automatic SSH deployment to UAT |
| `infra/docker/.env.uat.example` | Exists | Committable UAT env template |
| `infra/docker/.env.uat` | Local ignored | Real secrets, do not commit |
| `infra/docker/docker-compose.uat.yml` | Exists | UAT stack: postgres, keycloak, app |
| `infra/docker/docker-compose.uat.local.yml` | Local/temp UAT | Publish MacBook ports for Cloudflare Tunnel |
| `infra/docker/postgres/uat-init/01-create-keycloak-db.sql` | Exists | Create separate Keycloak DB |
| `infra/docker/keycloak/import/gym-platform-realm.json` | Exists | Keycloak realm import |
| `.gitignore` | Exists | Prevent committing local secrets |

## 3. `gym-platform-api/Dockerfile`

This file turns Spring Boot source code into a Docker image.

Core idea:

```dockerfile
FROM eclipse-temurin:26-jdk AS build
RUN ./mvnw -q -B -DskipTests package
RUN java -Djarmode=tools -jar target/*.jar extract --layers --destination target/extracted

FROM eclipse-temurin:26-jre
COPY --from=build /workspace/target/extracted/application/ ./
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/*.jar"]
```

Explanation:
- Build stage uses JDK 26.
- Runtime stage uses JRE 26 to keep the image smaller.
- The app runs with `java -jar /app/*.jar`.
- `JAVA_OPTS` allows memory tuning without rebuilding.
- The container runs as non-root user `gym`.
- Healthcheck calls `/actuator/health`.

For a new project:
- Adjust module/context paths if not `gym-platform-api`.
- Keep Java version aligned with `pom.xml`.
- Build `linux/arm64` for Oracle ARM, `linux/amd64` for x86_64 VPS.

## 4. `.github/workflows/ci.yml`

This is the quality gate.

Current triggers:

```yaml
on:
  push:
    branches:
      - main
      - develop
      - "feature/**"
      - "codex/**"
  pull_request:
    branches:
      - main
      - develop
```

Main job:

```yaml
services:
  postgres:
    image: postgres:16-alpine

defaults:
  run:
    working-directory: gym-platform-api

steps:
  - uses: actions/checkout@v4
  - uses: actions/setup-java@v4
  - run: ./mvnw -q -B verify
```

Explanation:
- CI starts PostgreSQL so Spring/Flyway tests use a real DB.
- `working-directory: gym-platform-api` because the Maven project is in a subdirectory.
- `./mvnw -q -B verify` runs Maven verification.
- Keycloak env values let the app boot in tests without running a real Keycloak container.

For a new project:
- Adjust `working-directory`.
- Adjust DB env names.
- Keep PR triggers for the integration branch.

## 5. `.github/workflows/docker-image.yml`

This file builds and pushes the API image to GHCR.

Current trigger:

```yaml
on:
  push:
    branches:
      - main
  workflow_dispatch:
```

Build section:

```yaml
- name: Docker metadata
  id: meta
  uses: docker/metadata-action@v5
  with:
    images: ghcr.io/diepchu1999/gym-platform-api
    tags: |
      type=sha,format=short
      type=ref,event=branch

- name: Build and push
  uses: docker/build-push-action@v6
  with:
    context: ./gym-platform-api
    platforms: linux/arm64
    push: true
```

Explanation:
- `packages: write` allows pushing to GHCR.
- `docker/login-action` logs into GHCR with `GITHUB_TOKEN`.
- `type=sha,format=short` creates tags like `sha-f74088d`.
- `platforms: linux/arm64` fits Oracle ARM/Mac ARM.

For a new project:
- Change `images:`.
- Change `context:` if Dockerfile is elsewhere.
- For x86_64 VPS:

```yaml
platforms: linux/amd64
```

For both ARM and x86:

```yaml
platforms: linux/amd64,linux/arm64
```

To build UAT images from `develop`:

```yaml
on:
  push:
    branches:
      - main
      - develop
```

## 6. Target `.github/workflows/cd.yml`

The repo does not have `cd.yml` yet. This is the target file when the team has a real VM/VPS and wants automatic deployment.

Required GitHub Secrets:

```text
UAT_HOST              UAT public IP or hostname
UAT_USER              SSH user, e.g. ubuntu
UAT_SSH_PRIVATE_KEY   Private key for GitHub Actions SSH
UAT_APP_DIR           Server directory, e.g. /opt/gym-platform
```

Reference template:

```yaml
name: Deploy UAT

on:
  workflow_run:
    workflows:
      - Docker Image
    types:
      - completed
    branches:
      - develop

permissions:
  contents: read

jobs:
  deploy:
    name: Deploy UAT
    if: ${{ github.event.workflow_run.conclusion == 'success' }}
    runs-on: ubuntu-latest

    steps:
      - name: Deploy over SSH
        uses: appleboy/ssh-action@v1.2.0
        with:
          host: ${{ secrets.UAT_HOST }}
          username: ${{ secrets.UAT_USER }}
          key: ${{ secrets.UAT_SSH_PRIVATE_KEY }}
          script: |
            cd "${{ secrets.UAT_APP_DIR }}"
            git fetch origin develop
            git checkout develop
            git pull origin develop
            SHORT_SHA="$(git rev-parse --short HEAD)"
            sed -i "s/^APP_TAG=.*/APP_TAG=sha-${SHORT_SHA}/" infra/docker/.env.uat
            docker compose --env-file infra/docker/.env.uat -f infra/docker/docker-compose.uat.yml pull app
            docker compose --env-file infra/docker/.env.uat -f infra/docker/docker-compose.uat.yml up -d app
            docker compose --env-file infra/docker/.env.uat -f infra/docker/docker-compose.uat.yml ps
```

Notes:
- Adjust if UAT does not keep the full repo on the server.
- Do not use SSH CD for temporary MacBook + Cloudflare UAT.
- Never commit private keys; store them in GitHub Secrets.

## 7. `infra/docker/.env.uat.example`

This is the committable runtime template.

Important image values:

```env
GHCR_OWNER=your-github-owner
APP_TAG=uat
APP_JAVA_OPTS=-XX:MaxRAMPercentage=65.0 -XX:+ExitOnOutOfMemoryError
```

Database:

```env
POSTGRES_DB=gym_platform
POSTGRES_USER=gym
POSTGRES_PASSWORD=change-me-strong-db-password
```

Keycloak:

```env
KEYCLOAK_ISSUER_URI=https://replace-with-keycloak-public-host/realms/gym-platform
KEYCLOAK_JWK_SET_URI=http://keycloak:8080/realms/gym-platform/protocol/openid-connect/certs
KC_HOSTNAME=https://replace-with-keycloak-public-host
```

Important:
- `KEYCLOAK_ISSUER_URI` must match the token `iss` claim.
- `KC_HOSTNAME` is the public Keycloak URL without `/realms/...`.
- `KEYCLOAK_JWK_SET_URI` can use internal Docker DNS for stable key loading.

For a new project:
- Copy `.env.uat.example` to `.env.uat`.
- Fill real secrets in `.env.uat`.
- Do not commit `.env.uat`.

## 8. `infra/docker/.env.uat`

This file contains real secrets and lives only locally or on UAT.

Create it:

```bash
cp infra/docker/.env.uat.example infra/docker/.env.uat
```

Edit values:

```env
GHCR_OWNER=diepchu1999
APP_TAG=sha-f74088d
POSTGRES_PASSWORD=<real-password>
KEYCLOAK_ADMIN_PASSWORD=<real-password>
KEYCLOAK_ISSUER_URI=<public-keycloak-url>/realms/gym-platform
KC_HOSTNAME=<public-keycloak-url>
```

Verify it is ignored:

```bash
git status --short --ignored=matching infra/docker/.env.uat
```

Expected:

```text
!! infra/docker/.env.uat
```

## 9. `infra/docker/docker-compose.uat.yml`

This file defines the UAT stack.

Main services:

```text
postgres
keycloak
app
```

`postgres`:
- Uses `postgres:16-alpine`.
- Has persistent volume `postgres-data`.
- Mounts `./postgres/uat-init` to create the `keycloak` DB.
- Uses `pg_isready` healthcheck.

`keycloak`:
- Uses `quay.io/keycloak/keycloak:26.1`.
- Runs `start --import-realm`, not `start-dev`.
- Uses a separate Postgres DB named `keycloak`.
- Imports realm from `./keycloak/import`.
- Uses `KC_HOSTNAME` for correct public issuer URLs.

`app`:
- Image: `ghcr.io/${GHCR_OWNER}/gym-platform-api:${APP_TAG}`.
- Connects DB through Docker DNS `postgres:5432`.
- Reads JWK through Docker DNS `keycloak:8080`.
- Healthcheck is `/actuator/health`.
- Memory is capped with `mem_limit`.

For a new project:
- Change `name:`.
- Change image name.
- Change container names if running multiple stacks.
- Keep DB volumes.
- Do not hardcode passwords in compose; use `.env.uat`.

## 10. `infra/docker/docker-compose.uat.local.yml`

This file supports temporary MacBook + Cloudflare Tunnel UAT.

Content:

```yaml
services:
  app:
    ports:
      - "8080:8080"

  keycloak:
    ports:
      - "18085:8080"
```

Explanation:
- `localhost:8080` maps to app.
- `localhost:18085` maps to Keycloak.
- Cloudflare Tunnel needs host ports to expose local services publicly.

Run with override:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  up -d
```

If a port is busy:
- Change `8080:8080` to something like `18080:8080`.
- Point the tunnel to `http://localhost:18080`.

## 11. `infra/docker/postgres/uat-init/01-create-keycloak-db.sql`

This file runs when the Postgres volume is first created.

Goal:

```text
Create a separate keycloak database.
```

Why:
- App DB is `gym_platform`.
- Keycloak should keep auth data outside business schema.

Notes:
- Init scripts run only when the Postgres volume is new.
- Editing this file does not rerun it on an existing volume.

## 12. `infra/docker/keycloak/import/gym-platform-realm.json`

This file imports the `gym-platform` realm into Keycloak.

It contains:
- Realm `gym-platform`.
- Client `gym-dev-cli`.
- Realm roles such as `STAFF`.
- Dev users such as `superadmin`.

For a new project:
- Change realm name if needed.
- Change client id if needed.
- Do not store production passwords in realm import.
- Bootstrap users should be dev/UAT only.

Security note:
- Realm export may contain dev keys/certificates.
- Do not treat this dev import as a long-term production secret.

## 13. `.gitignore`

Ignore local secrets:

```gitignore
infra/docker/.env.uat
*.pem
*.key
```

Verify:

```bash
git check-ignore -v infra/docker/.env.uat
```

If there is no output, `.env.uat` is not ignored correctly.

## 14. Cloudflare Tunnel Config

Quick Tunnel does not need a config file:

```bash
cloudflared tunnel --url http://localhost:8080
cloudflared tunnel --url http://localhost:18085
```

Each run can produce a new URL:

```text
https://random-name.trycloudflare.com
```

When Keycloak URL changes, update:

```env
KEYCLOAK_ISSUER_URI=<new-kc-url>/realms/gym-platform
KC_HOSTNAME=<new-kc-url>
```

Named Tunnel is more stable but requires a domain and Cloudflare account.

## 15. Setting Up A New Project

Required files:

```text
[ ] App Dockerfile
[ ] .github/workflows/ci.yml
[ ] .github/workflows/docker-image.yml
[ ] infra/docker/.env.uat.example
[ ] infra/docker/docker-compose.uat.yml
[ ] infra/docker/docker-compose.uat.local.yml if using MacBook + Cloudflare
[ ] infra/docker/postgres/uat-init if multiple DBs are needed
[ ] keycloak/import/<realm>.json if using Keycloak
[ ] .gitignore ignores secrets
```

GitHub checklist:

```text
[ ] Actions enabled
[ ] Packages/GHCR available
[ ] Workflow has permissions packages: write
[ ] If using SSH CD: UAT_HOST, UAT_USER, UAT_SSH_PRIVATE_KEY, UAT_APP_DIR secrets exist
```

Runtime checklist:

```text
[ ] docker compose config passes
[ ] postgres healthy
[ ] keycloak healthy
[ ] app healthy
[ ] /actuator/health returns 200
[ ] Keycloak issuer matches token iss
[ ] Protected API returns 401 without token
[ ] Protected API returns expected 200/403 with token
```

## 16. Configuration Verification Commands

Render compose:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  config
```

List services:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  config --services
```

Expected:

```text
postgres
keycloak
app
```

Start stack:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  up -d
```

View app logs:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  logs --tail 120 app
```
