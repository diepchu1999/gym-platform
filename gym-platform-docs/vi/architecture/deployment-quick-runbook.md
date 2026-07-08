# Runbook Nhanh: Từ Commit Đến UAT

> Bản tiếng Việt (canonical). English: [`../../en/architecture/deployment-quick-runbook.md`](../../en/architecture/deployment-quick-runbook.md).
>
> Tài liệu này là bản "copy lệnh chạy theo". Nếu cần hiểu vì sao từng bước tồn tại, đọc [`deployment-workflow.md`](deployment-workflow.md). Nếu cần setup file config từ đầu, đọc [`deployment-configuration-guide.md`](deployment-configuration-guide.md).

## 0. Biến cần thay

Trước khi chạy, thay các giá trị này theo task hiện tại:

```bash
export BRANCH_NAME="GYM-XX-short-feature-name"
export COMMIT_MESSAGE="feat: short clear message"
```

Với UAT tạm bằng MacBook + Cloudflare, cần thêm:

```bash
export API_PUBLIC_URL="https://<api-tunnel>.trycloudflare.com"
export KC_PUBLIC_URL="https://<keycloak-tunnel>.trycloudflare.com"
```

## 1. Tạo branch từ develop

```bash
cd /Users/diepchu/project/gym-platform

git checkout develop
git pull origin develop
git checkout -b "$BRANCH_NAME"
```

Kiểm tra:

```bash
git branch --show-current
```

## 2. Code xong thì chạy test local

```bash
cd /Users/diepchu/project/gym-platform/gym-platform-api
./mvnw -q -B verify
```

Nếu chỉ muốn kiểm tra kiến trúc module:

```bash
./mvnw -q -B test -Dtest=com.gym.architecture.ArchitectureRulesTest
```

## 3. Xem diff trước khi commit

```bash
cd /Users/diepchu/project/gym-platform

git status
git diff
```

Không commit file secret như:

```text
infra/docker/.env.uat
*.key
*.pem
```

## 4. Commit

```bash
git add <file-1> <file-2>
git commit -m "$COMMIT_MESSAGE"
```

Kiểm tra commit mới nhất:

```bash
git log --oneline -5
```

## 5. Push branch

```bash
git push -u origin "$BRANCH_NAME"
```

## 6. Mở Pull Request

Thao tác trên GitHub:

```text
Pull requests
-> New pull request
-> base: develop
-> compare: GYM-XX-short-feature-name
-> Create pull request
```

Checklist PR:

```text
[ ] PR target là develop
[ ] Mô tả task làm gì
[ ] Ghi test đã chạy
[ ] Không có secret trong diff
```

## 7. Chờ CI

Vào tab **Checks** trong PR.

Kỳ vọng:

```text
API build and tests: success
```

Nếu đỏ:

```text
Actions
-> workflow CI
-> job API build and tests
-> mở step Verify
-> tìm dòng [ERROR] đầu tiên
```

Sửa lỗi rồi push lại:

```bash
git status
git add <files>
git commit -m "fix: address CI failure"
git push
```

## 8. Review và merge

Khi CI xanh và review OK, merge PR vào `develop`.

Khuyến nghị:

```text
Squash and merge
```

Sau khi merge, xóa branch nếu GitHub hỏi.

## 9. Build image GHCR

Hiện trạng repo ngày 2026-07-08:

```text
.github/workflows/docker-image.yml đang trigger khi push vào main.
CD tự động từ develop chưa có cd.yml.
```

Nếu team đã chỉnh workflow build image từ `develop`, chỉ cần chờ workflow xanh.

Nếu hiện vẫn build từ `main`, merge `develop` vào `main` theo quy trình team rồi chờ workflow **Docker Image** xanh.

Kiểm tra tag mới:

```bash
cd /Users/diepchu/project/gym-platform
git fetch origin main
git rev-parse --short origin/main
```

Ví dụ output:

```text
f74088d
```

Image tag tương ứng:

```text
sha-f74088d
```

## 10. Deploy UAT tạm trên MacBook

Đảm bảo đang ở repo:

```bash
cd /Users/diepchu/project/gym-platform
```

Đổi `APP_TAG` trong file local ignored:

```bash
perl -0pi -e 's/^APP_TAG=.*/APP_TAG=sha-f74088d/m' infra/docker/.env.uat
```

Thay `sha-f74088d` bằng tag thật của bạn.

Pull image mới:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  pull app
```

Start hoặc recreate UAT stack:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  up -d
```

Kiểm tra container:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  ps
```

Kỳ vọng:

```text
gym-uat-api        healthy
gym-uat-keycloak   healthy
gym-uat-postgres   healthy
```

## 11. Start Cloudflare Tunnel

Terminal 1, expose API:

```bash
cloudflared tunnel --url http://localhost:8080
```

Copy URL dạng:

```text
https://<api-tunnel>.trycloudflare.com
```

Terminal 2, expose Keycloak:

```bash
cloudflared tunnel --url http://localhost:18085
```

Copy URL dạng:

```text
https://<keycloak-tunnel>.trycloudflare.com
```

## 12. Cập nhật issuer Keycloak khi tunnel đổi

Nếu Keycloak tunnel URL đổi, cập nhật `.env.uat`:

```bash
perl -0pi -e "s|^KEYCLOAK_ISSUER_URI=.*|KEYCLOAK_ISSUER_URI=${KC_PUBLIC_URL}/realms/gym-platform|m" infra/docker/.env.uat
perl -0pi -e "s|^KC_HOSTNAME=.*|KC_HOSTNAME=${KC_PUBLIC_URL}|m" infra/docker/.env.uat
```

Restart Keycloak và app:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  up -d --force-recreate keycloak app
```

Verify issuer:

```bash
curl -s "${KC_PUBLIC_URL}/realms/gym-platform/.well-known/openid-configuration" \
  | grep -o '"issuer":"[^"]*"'
```

Kỳ vọng:

```text
"issuer":"https://<keycloak-tunnel>.trycloudflare.com/realms/gym-platform"
```

## 13. Smoke test sau deploy

Health:

```bash
curl -i "${API_PUBLIC_URL}/actuator/health"
```

Không token phải bị chặn:

```bash
curl -i "${API_PUBLIC_URL}/api/v1/admin/branches"
```

Kỳ vọng:

```text
401 Unauthorized
```

Lấy token bằng Postman OAuth2:

```text
Grant Type: Authorization Code (With PKCE)
Auth URL: <KC_PUBLIC_URL>/realms/gym-platform/protocol/openid-connect/auth
Access Token URL: <KC_PUBLIC_URL>/realms/gym-platform/protocol/openid-connect/token
Client ID: gym-dev-cli
Scope: openid profile email roles
Callback URL: https://oauth.pstmn.io/v1/callback
```

Gọi API có token:

```text
GET <API_PUBLIC_URL>/api/v1/me
GET <API_PUBLIC_URL>/api/v1/admin/package-plans
```

Deploy OK khi:

```text
[ ] /actuator/health trả 200
[ ] Không token trả 401
[ ] Lấy token Keycloak được
[ ] /api/v1/me trả 200
[ ] API theo quyền trả 200 hoặc 403 đúng expectation
```

## 14. Rollback nhanh

Đổi về tag cũ:

```bash
perl -0pi -e 's/^APP_TAG=.*/APP_TAG=sha-OLDGOOD/m' infra/docker/.env.uat
```

Pull và recreate app:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  pull app

docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  up -d app
```

Kiểm tra:

```bash
curl -i "${API_PUBLIC_URL}/actuator/health"
```

## 15. Shutdown UAT tạm

Tắt Cloudflare Tunnel bằng `Ctrl+C` trong 2 terminal tunnel.

Tắt Docker Compose:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  down
```

Nếu muốn xóa luôn volume DB local, chỉ dùng khi chắc chắn không cần dữ liệu:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  down -v
```
