# Module: CRM + Customer Care

## Purpose

Manage leads, trial conversion, follow-up, booking no-show call workflow, support tickets, and retention.

## Actors

- CSKH
- Sales
- Receptionist
- Branch Manager
- Marketing Staff
- Super Admin

## Lead Status

- NEW
- CONTACTED
- INTERESTED
- VISITED
- TRIAL_REGISTERED
- CONVERTED
- LOST

## Ticket Status

- NEW
- ASSIGNED
- IN_PROGRESS
- WAITING_CUSTOMER
- RESOLVED
- CLOSED

## Business Rules

- Trial customers should be followed up during trial period.
- Booking no-show creates CSKH task at booking start time when member has not checked in.
- CSKH can hold slot up to 30 minutes after calling customer.
- Customer care notes should be stored in member timeline.
- Complaints and refund requests should be tracked as tickets.

## Trial Follow-up Suggestion

- Day 1: Welcome and QR guide.
- Day 3: Suggest trial group class.
- Day 5: Offer package promotion.
- Day 7: Expiry reminder and conversion call.

## Suggested Data Fields

Lead:
- id
- full_name
- phone
- source
- interested_branch_id
- interested_service
- status
- assigned_to
- next_follow_up_at

Care Task:
- id
- member_id
- task_type
- assigned_to
- due_at
- status
- result
- note

Ticket:
- id
- member_id
- branch_id
- category
- priority
- status
- assigned_to
- description
- resolution

## API Suggestions

- `POST /leads`
- `GET /leads`
- `POST /leads/{id}/convert`
- `POST /care-tasks`
- `POST /care-tasks/{id}/complete`
- `POST /tickets`
- `POST /tickets/{id}/assign`
- `POST /tickets/{id}/resolve`

## Tests

- Create lead.
- Convert lead to member.
- Create no-show care task.
- Complete care task with result.
- Create and resolve ticket.
