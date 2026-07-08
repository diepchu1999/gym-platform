# Hướng Dẫn Cấu Hình Deploy UAT

> Bản tiếng Việt (canonical). English: [`../../en/architecture/deployment-configuration-guide.md`](../../en/architecture/deployment-configuration-guide.md).
>
> Tài liệu này giải thích từng file cấu hình cần để build, push image và chạy UAT. Nếu chỉ muốn copy lệnh chạy nhanh, đọc [`deployment-quick-runbook.md`](deployment-quick-runbook.md). Nếu muốn hiểu quy trình Git/CI/CD tổng thể, đọc [`deployment-workflow.md`](deployment-workflow.md).

## 1. Mục tiêu

Khi setup deploy cho gym-platform hoặc một project Spring Boot tương tự, cần trả lời 4 câu hỏi:

1. App được build bằng gì?
2. Image được lưu ở đâu?
3. Runtime cần những service nào?
4. Secret và URL public được cấu hình ở đâu?

Các file trong tài liệu này trả lời 4 câu đó.

## 2. Danh sách file deploy

| File | Trạng thái | Mục đích |
|---|---|---|
| `gym-platform-api/Dockerfile` | Đã có | Build image chạy Spring Boot API |
| `.github/workflows/ci.yml` | Đã có | Chạy test tự động khi push/PR |
| `.github/workflows/docker-image.yml` | Đã có | Build/push Docker image lên GHCR |
| `.github/workflows/cd.yml` | Chưa có | Target: SSH deploy UAT tự động |
| `infra/docker/.env.uat.example` | Đã có | Template env UAT, commit được |
| `infra/docker/.env.uat` | Local ignored | Secret thật, không commit |
| `infra/docker/docker-compose.uat.yml` | Đã có | Stack UAT: postgres, keycloak, app |
| `infra/docker/docker-compose.uat.local.yml` | Local/UAT tạm | Publish port MacBook cho Cloudflare Tunnel |
| `infra/docker/postgres/uat-init/01-create-keycloak-db.sql` | Đã có | Tạo DB riêng cho Keycloak |
| `infra/docker/keycloak/import/gym-platform-realm.json` | Đã có | Realm import cho Keycloak |
| `.gitignore` | Đã có | Chặn commit secret local |

## 3. `gym-platform-api/Dockerfile`

File này biến source Spring Boot thành Docker image.

Ý chính:

```dockerfile
FROM eclipse-temurin:26-jdk AS build
RUN ./mvnw -q -B -DskipTests package
RUN java -Djarmode=tools -jar target/*.jar extract --layers --destination target/extracted

FROM eclipse-temurin:26-jre
COPY --from=build /workspace/target/extracted/application/ ./
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/*.jar"]
```

Giải thích:
- Stage `build` dùng JDK 26 để compile/package.
- Stage runtime dùng JRE 26 để image nhẹ hơn.
- App chạy bằng `java -jar /app/*.jar`.
- `JAVA_OPTS` cho phép chỉnh RAM mà không cần rebuild image.
- Container chạy non-root user `gym`.
- Healthcheck gọi `/actuator/health`.

Khi setup project mới:
- Đổi tên module/context nếu khác `gym-platform-api`.
- Giữ Java version khớp `pom.xml`.
- Nếu server là x86_64, build image `linux/amd64`; nếu Oracle ARM, build `linux/arm64`.

## 4. `.github/workflows/ci.yml`

File này là cổng kiểm tra chất lượng.

Hiện trạng:

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

Job chính:

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

Giải thích:
- CI tạo PostgreSQL service để Spring/Flyway test có DB thật.
- `working-directory: gym-platform-api` vì Maven project nằm trong thư mục con.
- `./mvnw -q -B verify` chạy build/test theo Maven lifecycle.
- `KEYCLOAK_ISSUER_URI` và `KEYCLOAK_JWK_SET_URI` được set bằng env để app boot được trong test.

Khi setup project mới:
- Sửa `working-directory` nếu module API nằm nơi khác.
- Sửa DB env nếu app dùng DB name/user khác.
- Giữ `pull_request` vào nhánh tích hợp để PR luôn chạy CI.

## 5. `.github/workflows/docker-image.yml`

File này build image và push lên GHCR.

Hiện trạng:

```yaml
on:
  push:
    branches:
      - main
  workflow_dispatch:
```

Phần build:

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

Giải thích:
- `packages: write` cho phép workflow push image vào GHCR.
- `docker/login-action` đăng nhập GHCR bằng `GITHUB_TOKEN`.
- `type=sha,format=short` tạo tag như `sha-f74088d`.
- `platforms: linux/arm64` phù hợp Oracle ARM/Mac ARM.

Khi setup project mới:
- Đổi `images:` sang owner/repo mới.
- Đổi `context:` nếu Dockerfile nằm nơi khác.
- Nếu dùng VPS x86_64, đổi platform thành:

```yaml
platforms: linux/amd64
```

Nếu muốn image chạy được cả ARM và x86:

```yaml
platforms: linux/amd64,linux/arm64
```

Nếu muốn merge vào `develop` tự build image UAT, thêm `develop`:

```yaml
on:
  push:
    branches:
      - main
      - develop
```

## 6. Target `.github/workflows/cd.yml`

Repo hiện chưa có `cd.yml`. Đây là file target khi team có VM/VPS thật và muốn deploy tự động.

GitHub Secrets cần tạo:

```text
UAT_HOST              Public IP hoặc hostname của UAT server
UAT_USER              User SSH, ví dụ ubuntu
UAT_SSH_PRIVATE_KEY   Private key để GitHub Actions SSH vào server
UAT_APP_DIR           Thư mục repo/deploy trên server, ví dụ /opt/gym-platform
```

Template tham khảo:

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

Ghi chú:
- Template này cần chỉnh nếu UAT không lưu full repo trên server.
- Với MacBook + Cloudflare Tunnel tạm thời, chưa nên dùng SSH CD.
- Không commit private key; chỉ đưa vào GitHub Secrets.

## 7. `infra/docker/.env.uat.example`

File này là template commit được. Nó nói cho dev biết runtime cần những biến nào.

Nhóm quan trọng:

```env
GHCR_OWNER=your-github-owner
APP_TAG=uat
APP_JAVA_OPTS=-XX:MaxRAMPercentage=65.0 -XX:+ExitOnOutOfMemoryError
```

Giải thích:
- `GHCR_OWNER`: GitHub user/org chứa image.
- `APP_TAG`: tag image cần chạy, ưu tiên `sha-xxxxxxx`.
- `APP_JAVA_OPTS`: giới hạn JVM theo RAM container.

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

Giải thích quan trọng:
- `KEYCLOAK_ISSUER_URI` phải khớp claim `iss` trong token.
- `KC_HOSTNAME` là public URL của Keycloak, không bao gồm `/realms/...`.
- `KEYCLOAK_JWK_SET_URI` có thể dùng internal Docker DNS để app lấy public key nhanh và ổn định.

Khi setup project mới:
- Copy `.env.uat.example` thành `.env.uat`.
- Điền secret thật vào `.env.uat`.
- Không commit `.env.uat`.

## 8. `infra/docker/.env.uat`

File này chứa secret thật và chỉ nằm trên máy local hoặc server UAT.

Tạo file:

```bash
cp infra/docker/.env.uat.example infra/docker/.env.uat
```

Sửa các giá trị:

```env
GHCR_OWNER=diepchu1999
APP_TAG=sha-f74088d
POSTGRES_PASSWORD=<real-password>
KEYCLOAK_ADMIN_PASSWORD=<real-password>
KEYCLOAK_ISSUER_URI=<public-keycloak-url>/realms/gym-platform
KC_HOSTNAME=<public-keycloak-url>
```

Kiểm tra không bị commit:

```bash
git status --short --ignored=matching infra/docker/.env.uat
```

Kỳ vọng:

```text
!! infra/docker/.env.uat
```

## 9. `infra/docker/docker-compose.uat.yml`

File này định nghĩa UAT stack.

Service chính:

```text
postgres
keycloak
app
```

`postgres`:
- Dùng `postgres:16-alpine`.
- Có volume `postgres-data` để dữ liệu không mất khi recreate container.
- Mount `./postgres/uat-init` để tạo DB `keycloak`.
- Có healthcheck `pg_isready`.

`keycloak`:
- Dùng `quay.io/keycloak/keycloak:26.1`.
- Chạy `start --import-realm`, không dùng `start-dev`.
- Dùng Postgres DB riêng `keycloak`.
- Mount realm import từ `./keycloak/import`.
- Có `KC_HOSTNAME` để sinh issuer public đúng.

`app`:
- Image: `ghcr.io/${GHCR_OWNER}/gym-platform-api:${APP_TAG}`.
- Kết nối DB qua Docker DNS `postgres:5432`.
- Kết nối JWK qua Docker DNS `keycloak:8080`.
- Healthcheck `/actuator/health`.
- Giới hạn RAM bằng `mem_limit`.

Khi setup project mới:
- Đổi `name:` để tránh đụng project khác.
- Đổi image name.
- Đổi container name nếu cần chạy nhiều stack song song.
- Giữ volume cho database.
- Không hardcode password trong compose; dùng `.env.uat`.

## 10. `infra/docker/docker-compose.uat.local.yml`

File này phục vụ UAT tạm trên MacBook + Cloudflare Tunnel.

Nội dung:

```yaml
services:
  app:
    ports:
      - "8080:8080"

  keycloak:
    ports:
      - "18085:8080"
```

Giải thích:
- `localhost:8080` trỏ vào app.
- `localhost:18085` trỏ vào Keycloak.
- Cloudflare Tunnel cần host port để proxy public HTTPS vào máy local.

Chạy compose kèm override:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  up -d
```

Nếu port bị trùng:
- Đổi `8080:8080` thành port khác, ví dụ `18080:8080`.
- Đổi tunnel tương ứng sang `http://localhost:18080`.

## 11. `infra/docker/postgres/uat-init/01-create-keycloak-db.sql`

File này chạy khi Postgres volume được tạo lần đầu.

Mục tiêu:

```text
Tạo database keycloak riêng cho Keycloak.
```

Vì sao:
- App DB là `gym_platform`.
- Keycloak nên có DB riêng để dữ liệu auth không lẫn với business schema.

Lưu ý:
- Script init chỉ chạy khi volume Postgres mới.
- Nếu volume đã tồn tại, sửa file này không tự chạy lại.

## 12. `infra/docker/keycloak/import/gym-platform-realm.json`

File này import realm `gym-platform` vào Keycloak.

Nó chứa:
- Realm `gym-platform`.
- Client `gym-dev-cli`.
- Realm role như `STAFF`.
- User dev như `superadmin`.

Khi setup project mới:
- Đổi realm name nếu project khác.
- Đổi client id nếu project khác.
- Không để password production trong realm import.
- User bootstrap chỉ nên dùng cho dev/UAT.

Lưu ý bảo mật:
- Realm export có thể chứa key/certificate dev.
- Không dùng file dev này làm production secret lâu dài.

## 13. `.gitignore`

Phải ignore các file local secret:

```gitignore
infra/docker/.env.uat
*.pem
*.key
```

Kiểm tra:

```bash
git check-ignore -v infra/docker/.env.uat
```

Nếu không có output, nghĩa là `.env.uat` chưa được ignore đúng.

## 14. Cloudflare Tunnel config

Quick Tunnel không cần file config:

```bash
cloudflared tunnel --url http://localhost:8080
cloudflared tunnel --url http://localhost:18085
```

Mỗi lần chạy có thể nhận URL mới:

```text
https://random-name.trycloudflare.com
```

Khi Keycloak URL đổi, phải cập nhật:

```env
KEYCLOAK_ISSUER_URI=<new-kc-url>/realms/gym-platform
KC_HOSTNAME=<new-kc-url>
```

Named Tunnel ổn định hơn nhưng cần domain riêng và Cloudflare account.

## 15. Setup deploy cho project mới

Checklist file cần tạo:

```text
[ ] Dockerfile cho app
[ ] .github/workflows/ci.yml
[ ] .github/workflows/docker-image.yml
[ ] infra/docker/.env.uat.example
[ ] infra/docker/docker-compose.uat.yml
[ ] infra/docker/docker-compose.uat.local.yml nếu dùng MacBook + Cloudflare
[ ] infra/docker/postgres/uat-init nếu cần nhiều DB
[ ] keycloak/import/<realm>.json nếu dùng Keycloak
[ ] .gitignore ignore secret
```

Checklist GitHub:

```text
[ ] Repo có Actions enabled
[ ] Packages/GHCR dùng được
[ ] Workflow có permissions packages: write
[ ] Nếu có CD SSH: tạo GitHub Secrets UAT_HOST, UAT_USER, UAT_SSH_PRIVATE_KEY, UAT_APP_DIR
```

Checklist runtime:

```text
[ ] docker compose config pass
[ ] postgres healthy
[ ] keycloak healthy
[ ] app healthy
[ ] /actuator/health trả 200
[ ] Keycloak issuer khớp token iss
[ ] API protected trả 401 khi không token
[ ] API protected trả 200/403 đúng quyền khi có token
```

## 16. Lệnh verify cấu hình

Render compose:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  config
```

Liệt kê service:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  config --services
```

Kỳ vọng:

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

Xem logs:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  logs --tail 120 app
```
