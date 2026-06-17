-- P1 Seed RBAC (schema: identity). Ref: data-model/p1-identity-org.md

INSERT INTO identity.rbac_role (code, name, scope, is_system) VALUES
    ('SUPER_ADMIN',       'Super Admin',        'GLOBAL', true),
    ('OPERATION_MANAGER', 'Operation Manager',  'GLOBAL', true),
    ('BRANCH_MANAGER',    'Branch Manager',     'BRANCH', true),
    ('RECEPTIONIST',      'Receptionist',       'BRANCH', true),
    ('SALES',             'Sales',              'BRANCH', true),
    ('CUSTOMER_CARE',     'Customer Care',      'BRANCH', true),
    ('PERSONAL_TRAINER',  'Personal Trainer',   'BRANCH', true),
    ('CLASS_INSTRUCTOR',  'Class Instructor',   'BRANCH', true),
    ('MASSAGE_STAFF',     'Massage Staff',      'BRANCH', true),
    ('CLEANER',           'Cleaner',            'BRANCH', true),
    ('PARKING_STAFF',     'Parking Staff',      'BRANCH', true),
    ('MAINTENANCE_STAFF', 'Maintenance Staff',  'BRANCH', true),
    ('ACCOUNTANT',        'Accountant',         'BRANCH', true),
    ('MARKETING_STAFF',   'Marketing Staff',    'BRANCH', true),
    ('PARTNER_MANAGER',   'Partner Manager',    'BRANCH', true);

INSERT INTO identity.rbac_permission (code, module, description) VALUES
    ('MEMBER_CREATE',         'member',     'Create member'),
    ('MEMBER_VIEW',           'member',     'View member'),
    ('MEMBER_VIEW_FULL_CCCD', 'kyc',        'View full CCCD (sensitive)'),
    ('KYC_APPROVE',           'kyc',        'Approve/reject KYC'),
    ('PACKAGE_SELL',          'membership', 'Sell package / create contract'),
    ('POS_SELL',              'inventory',  'Sell POS product'),
    ('PANTRY_SELL',           'pantry',     'Sell pantry item'),
    ('CHECKIN_SUPPORT',       'checkin',    'Support QR check-in'),
    ('BOOKING_MANAGE',        'booking',    'Manage bookings'),
    ('PT_MANAGE_OWN',         'pt',         'PT manage own schedule/customers'),
    ('RATING_VIEW_AUTHOR',    'rating',     'View rating author (manager only)'),
    ('MAINTENANCE_MANAGE',    'equipment',  'Manage maintenance tickets'),
    ('INVENTORY_MANAGE',      'inventory',  'Manage stock/import/transfer/adjust'),
    ('REPORT_VIEW',           'report',     'View reports'),
    ('AUDIT_VIEW',            'audit',      'View audit log'),
    ('STAFF_MANAGE',          'staff',      'Manage staff'),
    ('RBAC_MANAGE',           'identity',   'Manage roles/permissions');

INSERT INTO identity.rbac_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM identity.rbac_role r
CROSS JOIN identity.rbac_permission p
WHERE r.code = 'SUPER_ADMIN';
