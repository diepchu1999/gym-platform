# Module: Staff + RBAC

## Purpose

Manage staff profile, role, branch assignment, and access permission.

## Roles

- Super Admin
- Operation Manager
- Branch Manager
- Receptionist
- Sales
- Customer Care
- Personal Trainer
- Class Instructor
- Massage Staff
- Cleaner
- Parking Staff
- Maintenance Staff
- Accountant
- Marketing Staff
- Partner Manager

## Business Rules

- Super Admin can access all branches.
- Branch Manager can access assigned branches only.
- Receptionist can create member, sell package, support check-in, and sell POS items at assigned branch.
- CSKH can manage tickets, follow-up, and no-show calls.
- PT can see own schedule and own assigned customers.
- PT cannot see rating author.
- Manager can see rating author for internal handling.
- Maintenance staff can manage assigned maintenance tickets.

## Suggested Data Fields

Staff:
- id
- user_account_id
- full_name
- phone
- email
- employee_code
- status

Role:
- id
- code
- name

Permission:
- id
- code
- description

Staff Branch Assignment:
- staff_id
- branch_id
- role_id
- active

## API Suggestions

- `POST /staff`
- `GET /staff`
- `PATCH /staff/{id}`
- `POST /staff/{id}/roles`
- `POST /staff/{id}/branch-assignments`
- `GET /permissions`

## Tests

- Branch manager cannot access another branch.
- PT cannot view rating author.
- Manager can view rating author.
- Receptionist can create member at assigned branch.
