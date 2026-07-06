-- Seed role -> permission grants.
-- Do not hardcode numeric ids: role/permission ids are resolved by stable business code.
-- Idempotent: ON CONFLICT matches PK(identity.rbac_role_permission(role_id, permission_id)).

INSERT INTO identity.rbac_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM (VALUES
          ('OPERATION_MANAGER', 'STAFF_MANAGE'),
          ('OPERATION_MANAGER', 'RBAC_MANAGE'),
          ('OPERATION_MANAGER', 'REPORT_VIEW'),
          ('OPERATION_MANAGER', 'AUDIT_VIEW'),
          ('OPERATION_MANAGER', 'MEMBER_VIEW'),
          ('OPERATION_MANAGER', 'MEMBER_VIEW_FULL_CCCD'),
          ('OPERATION_MANAGER', 'PACKAGE_SELL'),
          ('OPERATION_MANAGER', 'BOOKING_MANAGE'),
          ('OPERATION_MANAGER', 'CHECKIN_SUPPORT'),
          ('OPERATION_MANAGER', 'INVENTORY_MANAGE'),
          ('OPERATION_MANAGER', 'MAINTENANCE_MANAGE'),
          ('OPERATION_MANAGER', 'RATING_VIEW_AUTHOR'),

          ('BRANCH_MANAGER', 'MEMBER_VIEW'),
          ('BRANCH_MANAGER', 'MEMBER_VIEW_FULL_CCCD'),
          ('BRANCH_MANAGER', 'PACKAGE_SELL'),
          ('BRANCH_MANAGER', 'POS_SELL'),
          ('BRANCH_MANAGER', 'PANTRY_SELL'),
          ('BRANCH_MANAGER', 'BOOKING_MANAGE'),
          ('BRANCH_MANAGER', 'CHECKIN_SUPPORT'),
          ('BRANCH_MANAGER', 'REPORT_VIEW'),
          ('BRANCH_MANAGER', 'INVENTORY_MANAGE'),
          ('BRANCH_MANAGER', 'MAINTENANCE_MANAGE'),
          ('BRANCH_MANAGER', 'RATING_VIEW_AUTHOR'),

          ('RECEPTIONIST', 'MEMBER_CREATE'),
          ('RECEPTIONIST', 'MEMBER_VIEW'),
          ('RECEPTIONIST', 'PACKAGE_SELL'),
          ('RECEPTIONIST', 'POS_SELL'),
          ('RECEPTIONIST', 'PANTRY_SELL'),
          ('RECEPTIONIST', 'CHECKIN_SUPPORT'),
          ('RECEPTIONIST', 'BOOKING_MANAGE'),

          ('SALES', 'MEMBER_CREATE'),
          ('SALES', 'MEMBER_VIEW'),
          ('SALES', 'PACKAGE_SELL'),

          ('CUSTOMER_CARE', 'MEMBER_VIEW'),
          ('CUSTOMER_CARE', 'BOOKING_MANAGE'),

          ('PERSONAL_TRAINER', 'PT_MANAGE_OWN'),

          ('MAINTENANCE_STAFF', 'MAINTENANCE_MANAGE'),

          ('ACCOUNTANT', 'REPORT_VIEW'),
          ('ACCOUNTANT', 'AUDIT_VIEW'),

          ('MARKETING_STAFF', 'REPORT_VIEW'),

          ('PARTNER_MANAGER', 'INVENTORY_MANAGE')
     ) AS grant_matrix(role_code, permission_code)
         JOIN identity.rbac_role r ON r.code = grant_matrix.role_code
         JOIN identity.rbac_permission p ON p.code = grant_matrix.permission_code
ON CONFLICT (role_id, permission_id) DO NOTHING;
