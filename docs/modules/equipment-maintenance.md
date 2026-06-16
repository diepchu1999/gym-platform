# Module: Equipment + Maintenance

## Purpose

Manage gym equipment, maintenance schedule, broken reports, maintenance tickets, and maintenance history.

## Actors

- Member
- Receptionist
- Maintenance Staff
- Branch Manager
- Super Admin

## Business Rules

- Equipment is tracked by branch and location.
- Equipment should have status and maintenance history.
- Staff/member can report broken equipment.
- Broken equipment creates maintenance ticket.
- Maintenance staff updates ticket and equipment status.

## Equipment Status

- ACTIVE
- NEED_MAINTENANCE
- UNDER_MAINTENANCE
- BROKEN
- RETIRED

## Maintenance Ticket Status

- NEW
- ASSIGNED
- IN_PROGRESS
- RESOLVED
- CLOSED

## Main Flow

1. Staff/admin creates equipment asset.
2. Equipment is assigned to branch/room/area.
3. Staff/member reports issue.
4. Maintenance ticket is created.
5. Maintenance staff accepts/starts work.
6. Equipment status becomes UNDER_MAINTENANCE.
7. Staff resolves ticket and records cost/note.
8. Equipment returns ACTIVE or becomes RETIRED.

## Suggested Data Fields

Equipment Asset:
- id
- asset_code
- name
- category
- branch_id
- room_id
- area
- status
- purchase_date
- supplier
- next_maintenance_date

Maintenance Ticket:
- id
- equipment_id
- branch_id
- reported_by
- assigned_to
- issue_description
- status
- priority
- cost
- resolved_at

Maintenance History:
- id
- equipment_id
- ticket_id
- action
- note
- cost
- performed_by
- performed_at

## API Suggestions

- `POST /equipment`
- `GET /equipment`
- `PATCH /equipment/{id}`
- `POST /equipment/{id}/report-issue`
- `GET /maintenance-tickets`
- `POST /maintenance-tickets/{id}/assign`
- `POST /maintenance-tickets/{id}/start`
- `POST /maintenance-tickets/{id}/resolve`

## Product Idea

Attach QR code to each equipment item. Member/staff can scan QR to report issue with image/video.

## Tests

- Create equipment.
- Report issue creates ticket.
- Start maintenance updates equipment status.
- Resolve ticket updates equipment status and history.
