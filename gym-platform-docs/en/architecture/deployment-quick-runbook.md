# Quick Runbook: From Commit To UAT

> This is the "copy commands and run" document. For explanations, read [`deployment-workflow.md`](deployment-workflow.md). For setup/configuration details, read [`deployment-configuration-guide.md`](deployment-configuration-guide.md).

## 0. Variables To Replace

```bash
export BRANCH_NAME="GYM-XX-short-feature-name"
export COMMIT_MESSAGE="feat: short clear message"
```

For temporary MacBook + Cloudflare UAT:

```bash
export API_PUBLIC_URL="https://<api-tunnel>.trycloudflare.com"
export KC_PUBLIC_URL="https://<keycloak-tunnel>.trycloudflare.com"
```

## 1. Create A Branch From develop

```bash
cd /Users/diepchu/project/gym-platform

git checkout develop
git pull origin develop
git checkout -b "$BRANCH_NAME"
```

Verify:

```bash
git branch --show-current
```

## 2. Run Local Tests

```bash
cd /Users/diepchu/project/gym-platform/gym-platform-api
./mvnw -q -B verify
```

Architecture guardrail only:

```bash
./mvnw -q -B test -Dtest=com.gym.architecture.ArchitectureRulesTest
```

## 3. Review Diff Before Commit

```bash
cd /Users/diepchu/project/gym-platform

git status
git diff
```

Do not commit secrets:

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

Verify:

```bash
git log --oneline -5
```

## 5. Push Branch

```bash
git push -u origin "$BRANCH_NAME"
```

## 6. Open Pull Request

On GitHub:

```text
Pull requests
-> New pull request
-> base: develop
-> compare: GYM-XX-short-feature-name
-> Create pull request
```

PR checklist:

```text
[ ] PR targets develop
[ ] Description explains the task
[ ] Verification is listed
[ ] No secrets in diff
```

## 7. Wait For CI

Expected:

```text
API build and tests: success
```

If red:

```text
Actions
-> CI workflow
-> API build and tests
-> Verify step
-> find the first [ERROR]
```

Fix and push again:

```bash
git status
git add <files>
git commit -m "fix: address CI failure"
git push
```

## 8. Review And Merge

After green CI and approval, merge the PR into `develop`.

Recommended:

```text
Squash and merge
```

## 9. Build GHCR Image

Repository state on 2026-07-08:

```text
.github/workflows/docker-image.yml currently triggers on main.
Automatic CD from develop does not exist yet.
```

If the team has changed image build to run from `develop`, wait for the workflow to pass.

If image build still runs from `main`, merge `develop` into `main` according to the team process and wait for **Docker Image** to pass.

Find the latest short SHA:

```bash
cd /Users/diepchu/project/gym-platform
git fetch origin main
git rev-parse --short origin/main
```

Example:

```text
f74088d
```

Image tag:

```text
sha-f74088d
```

## 10. Deploy Temporary MacBook UAT

```bash
cd /Users/diepchu/project/gym-platform
```

Update `APP_TAG`:

```bash
perl -0pi -e 's/^APP_TAG=.*/APP_TAG=sha-f74088d/m' infra/docker/.env.uat
```

Replace `sha-f74088d` with the real tag.

Pull the image:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  pull app
```

Start/recreate UAT:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  up -d
```

Check containers:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  ps
```

Expected:

```text
gym-uat-api        healthy
gym-uat-keycloak   healthy
gym-uat-postgres   healthy
```

## 11. Start Cloudflare Tunnels

Terminal 1, API:

```bash
cloudflared tunnel --url http://localhost:8080
```

Copy:

```text
https://<api-tunnel>.trycloudflare.com
```

Terminal 2, Keycloak:

```bash
cloudflared tunnel --url http://localhost:18085
```

Copy:

```text
https://<keycloak-tunnel>.trycloudflare.com
```

## 12. Update Keycloak Issuer When Tunnel Changes

```bash
perl -0pi -e "s|^KEYCLOAK_ISSUER_URI=.*|KEYCLOAK_ISSUER_URI=${KC_PUBLIC_URL}/realms/gym-platform|m" infra/docker/.env.uat
perl -0pi -e "s|^KC_HOSTNAME=.*|KC_HOSTNAME=${KC_PUBLIC_URL}|m" infra/docker/.env.uat
```

Restart Keycloak and app:

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

## 13. Post-Deploy Smoke Test

Health:

```bash
curl -i "${API_PUBLIC_URL}/actuator/health"
```

No token should be blocked:

```bash
curl -i "${API_PUBLIC_URL}/api/v1/admin/branches"
```

Expected:

```text
401 Unauthorized
```

Postman OAuth2:

```text
Grant Type: Authorization Code (With PKCE)
Auth URL: <KC_PUBLIC_URL>/realms/gym-platform/protocol/openid-connect/auth
Access Token URL: <KC_PUBLIC_URL>/realms/gym-platform/protocol/openid-connect/token
Client ID: gym-dev-cli
Scope: openid profile email roles
Callback URL: https://oauth.pstmn.io/v1/callback
```

Call with token:

```text
GET <API_PUBLIC_URL>/api/v1/me
GET <API_PUBLIC_URL>/api/v1/admin/package-plans
```

Deployment is OK when:

```text
[ ] /actuator/health returns 200
[ ] No-token request returns 401
[ ] Keycloak token can be obtained
[ ] /api/v1/me returns 200
[ ] Protected APIs return expected 200 or 403
```

## 14. Quick Rollback

```bash
perl -0pi -e 's/^APP_TAG=.*/APP_TAG=sha-OLDGOOD/m' infra/docker/.env.uat
```

Pull and recreate app:

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

Verify:

```bash
curl -i "${API_PUBLIC_URL}/actuator/health"
```

## 15. Shutdown Temporary UAT

Stop both Cloudflare tunnel terminals with `Ctrl+C`.

Stop Docker Compose:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  down
```

Remove local DB volumes only if you do not need data:

```bash
docker compose \
  --env-file infra/docker/.env.uat \
  -f infra/docker/docker-compose.uat.yml \
  -f infra/docker/docker-compose.uat.local.yml \
  down -v
```
