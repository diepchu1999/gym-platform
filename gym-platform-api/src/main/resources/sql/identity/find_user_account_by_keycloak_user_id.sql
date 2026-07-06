SELECT
    id,
    keycloak_user_id,
    account_type,
    username,
    email,
    status
FROM identity.identity_user_account
WHERE keycloak_user_id = :keycloakUserId
