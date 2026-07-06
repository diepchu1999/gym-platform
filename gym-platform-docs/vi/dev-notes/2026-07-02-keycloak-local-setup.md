# 2026-07-02 — Keycloak local setup cho S0.1

## Mục tiêu

Thiết lập Keycloak local cho epic security S0.1:

- Keycloak chạy bằng Docker Compose ở `http://localhost:8085`.
- Realm `gym-platform` có thể tạo lại từ file export.
- Client `gym-admin-web` dùng cho Admin Web local.
- User dev `staff.dev` dùng để smoke test lấy JWT.
- Realm config được lưu vào repo, không phụ thuộc vào Docker volume cục bộ.

## File liên quan

- `infra/docker/docker-compose.yml`
- `infra/docker/keycloak/import/gym-platform-realm.json`
- `.env.example`

## Biến môi trường local

Trong `.env.example` hoặc `.env` local:

```env
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOAK_PORT=8085
```

## Docker Compose service

Service `keycloak` trong `infra/docker/docker-compose.yml` cần có cấu hình chính:

```yaml
keycloak:
  image: quay.io/keycloak/keycloak:26.1
  container_name: gym-platform-keycloak
  restart: unless-stopped
  command: ["start-dev", "--import-realm"]
  environment:
    KC_BOOTSTRAP_ADMIN_USERNAME: ${KEYCLOAK_ADMIN:-admin}
    KC_BOOTSTRAP_ADMIN_PASSWORD: ${KEYCLOAK_ADMIN_PASSWORD:-admin}
    KC_HTTP_ENABLED: "true"
    KC_HEALTH_ENABLED: "true"
  ports:
    - "${KEYCLOAK_PORT:-8085}:8080"
  volumes:
    - keycloak-data:/opt/keycloak/data
    - ./keycloak/import:/opt/keycloak/data/import:ro
  networks: [gym-platform-net]
```

Ý nghĩa:

- `start-dev`: chạy Keycloak cho môi trường local.
- `--import-realm`: tự import realm từ `/opt/keycloak/data/import` khi start.
- `keycloak-data`: lưu DB nội bộ của Keycloak.
- `./keycloak/import`: nơi chứa realm export trong repo.
- `8085:8080`: host truy cập `localhost:8085`, container vẫn nghe cổng `8080`.

## Realm cần tạo trên UI

Mở:

```text
http://localhost:8085
```

Login admin:

```text
admin / admin
```

Tạo realm:

```text
Realm name: gym-platform
Enabled: ON
```

Tạo realm role:

```text
Role name: STAFF
Description: Coarse-grained gate for staff users
```

Role `STAFF` chỉ là cổng xác thực/phân quyền thô. Branch-scoped authorization vẫn nằm trong app theo ADR-0006.

## Client `gym-admin-web`

Tạo client:

```text
Client type: OpenID Connect
Client ID: gym-admin-web
Name: Gym Admin Web
```

Capability config:

```text
Client authentication: OFF
Authorization: OFF
Standard flow: ON
Direct access grants: ON
Implicit flow: OFF
Service accounts roles: OFF
```

Login settings:

```text
Valid redirect URIs:
http://localhost:5173/*

Web origins:
http://localhost:5173
```

PKCE:

```text
Proof Key for Code Exchange Code Challenge Method: S256
```

Ghi chú:

- `5173` là port dev server phổ biến của Vite Admin Web.
- Không whitelist thêm host/port nếu chưa dùng thật.

## User dev `staff.dev`

Tạo user:

```text
Username: staff.dev
Email: staff.dev@gym-platform.local
First name: Staff
Last name: Dev
Email verified: ON
Enabled: ON
```

Gán role:

```text
Realm role: STAFF
```

Set password local để smoke test:

```text
Password: staff.dev.123
Temporary: OFF
```

Không commit password hoặc credential hash vào realm export.

## Smoke test bằng Postman

Tạo request:

```text
Method: POST
URL: http://localhost:8085/realms/gym-platform/protocol/openid-connect/token
```

Body chọn `x-www-form-urlencoded`:

```text
grant_type=password
client_id=gym-admin-web
username=staff.dev
password=staff.dev.123
```

Kết quả đúng:

```json
{
  "access_token": "...",
  "expires_in": 300,
  "refresh_token": "...",
  "token_type": "Bearer"
}
```

Decode `access_token`, payload cần có:

```json
"realm_access": {
  "roles": ["STAFF"]
}
```

`grant_type=password` chỉ dùng cho dev smoke test. Frontend thật nên dùng Authorization Code + PKCE.

## Export realm

Không export bằng `docker compose exec keycloak kc.sh export` khi Keycloak đang chạy, vì DB H2 có thể bị lock.

Flow đúng:

```bash
cd /Users/diepchu/project/gym-platform

mkdir -p infra/docker/keycloak/import

docker compose -f infra/docker/docker-compose.yml stop keycloak

docker compose -f infra/docker/docker-compose.yml run --rm --no-deps \
  -v "$PWD/infra/docker/keycloak/import:/tmp/keycloak-export" \
  keycloak export \
  --realm gym-platform \
  --file /tmp/keycloak-export/gym-platform-realm.json

docker compose -f infra/docker/docker-compose.yml up -d keycloak
```

Kiểm tra file:

```bash
ls -lh infra/docker/keycloak/import/gym-platform-realm.json
```

Kiểm tra JSON hợp lệ:

```bash
python3 -m json.tool infra/docker/keycloak/import/gym-platform-realm.json >/tmp/gym-platform-realm.pretty.json
```

Kiểm tra credential không bị commit:

```bash
grep -n "credentials\" : \\[\\|secretData\\|credentialData" \
  infra/docker/keycloak/import/gym-platform-realm.json
```

Kỳ vọng: không còn `credentials`, `secretData`, `credentialData` của user test.

## Test import lại từ đầu

Reset chỉ Keycloak:

```bash
cd /Users/diepchu/project/gym-platform

docker compose -f infra/docker/docker-compose.yml stop keycloak
docker compose -f infra/docker/docker-compose.yml rm -f keycloak
docker volume rm gym-platform-local_keycloak-data
docker compose -f infra/docker/docker-compose.yml up -d keycloak
```

Sau khi Keycloak boot xong:

1. Mở `http://localhost:8085`.
2. Login `admin / admin`.
3. Kiểm tra realm `gym-platform` tự xuất hiện.
4. Kiểm tra client `gym-admin-web`.
5. Kiểm tra user `staff.dev`.
6. Set lại password cho `staff.dev` nếu export đã bỏ credential.
7. Smoke token bằng Postman.

Nếu realm tự xuất hiện sau khi xóa volume, S0.1 hoàn tất.

## Troubleshooting

### Export lỗi `Database may be already in use`

Nguyên nhân: chạy export trong lúc Keycloak server đang giữ lock DB H2.

Cách xử lý:

- Stop service `keycloak`.
- Export bằng container one-shot qua `docker compose run --rm --no-deps`.
- Start lại service `keycloak`.

### Import không thấy realm `gym-platform`

Nguyên nhân thường gặp:

- File export không nằm trong `infra/docker/keycloak/import`.
- Compose chưa mount `./keycloak/import:/opt/keycloak/data/import:ro`.
- Compose chưa có `--import-realm`.
- Volume Keycloak cũ đã có realm nên Keycloak bỏ qua import existing realm.

Cách kiểm tra:

```bash
docker compose -f infra/docker/docker-compose.yml logs --tail=120 keycloak
```

Log import thành công thường có nội dung:

```text
Realm 'gym-platform' imported
Import finished successfully
```

### User import xong không đăng nhập được

Nếu đã xóa credential khỏi export thì user `staff.dev` tồn tại nhưng chưa có password.

Cách xử lý:

- Vào Keycloak UI.
- Mở user `staff.dev`.
- Tab `Credentials`.
- Set password `staff.dev.123`.
- Temporary: `OFF`.

## Bước tiếp theo

Sau S0.1, tiếp tục S0.2:

- Thêm OAuth2 Resource Server dependency cho Spring Boot.
- Cấu hình JWT issuer/JWKS.
- Tạo security baseline cho `/api/v1/admin/**`.
- Thêm endpoint smoke `GET /api/v1/me`.
