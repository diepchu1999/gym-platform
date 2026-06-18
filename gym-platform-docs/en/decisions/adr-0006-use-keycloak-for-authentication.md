# ADR-0006: Use Keycloak for Authentication

> English version. Vietnamese (canonical): [`../../vi/decisions/adr-0006-use-keycloak-for-authentication.md`](../../vi/decisions/adr-0006-use-keycloak-for-authentication.md).

## Status
Proposed

## Context
The platform needs login, token issuance, password policy, session management, and later MFA/OTP/social login for both staff and members across multiple branches. Building and maintaining a secure in-house auth (password hashing, token rotation, account recovery) is costly and risky. Authorization in this system is **branch-scoped** (e.g. Branch Manager only at assigned branches), which realm roles alone cannot express cleanly.

## Decision
Use **Keycloak** as the OIDC/OAuth2 Identity Provider for **authentication only**. The Spring Boot monolith acts as an **OAuth2 resource server** validating JWTs via Keycloak's JWKS. **Branch-scoped authorization stays in the application** (`rbac_*` + `staff_branch_assignment`). The app DB stores no passwords; `identity_user_account` maps internal principals to the Keycloak `sub` (`keycloak_user_id`).

## Consequences
Positive: offload credential security, MFA, sessions; standard OIDC; SSO across admin/member web. Trade-offs: extra infra to run (Keycloak + its DB); identity becomes external (need mapping + provisioning sync); local dev needs a realm setup.

## Rules
- Realm `gym-platform`; clients `gym-admin-web`, `gym-member-web` (public + PKCE); API = resource server.
- Validate JWT via `issuer-uri` + JWKS; never trust client-supplied roles for branch authorization.
- Keep fine-grained, branch-scoped permission checks in the application layer.
- Supersedes the password-based identity assumption in `data-model/p1-identity-org.md`.
