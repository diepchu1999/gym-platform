INSERT INTO identity.identity_user_account (
    keycloak_user_id,
    account_type,
    username,
    email,
    status
) VALUES (
    :keycloakUserId,
    :accountType,
    :username,
    :email,
    'ACTIVE'
)
RETURNING
    id,
    keycloak_user_id,
    account_type,
    username,
    email,
    status
