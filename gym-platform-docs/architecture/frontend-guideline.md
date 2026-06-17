# Frontend Guideline

## Technical Baseline
- React.
- TypeScript.
- Web application first.
- No mobile app in initial phase.
- Use modern React patterns with functional components and hooks.

## Application Targets
Initial web targets:
- Admin web.
- Staff/branch operation screens.
- Member-facing web can be added after core admin flows.

Admin and member web may be:
- One React app with role-based route groups in early phase.
- Separate apps later if product scale requires it.

## Recommended Structure

```text
src/
 ├── app/                 # app bootstrap, router, providers
 ├── shared/              # shared UI, utils, hooks, API client
 ├── features/
 │   ├── member/
 │   ├── kyc/
 │   ├── package/
 │   ├── contract/
 │   ├── payment/
 │   ├── checkin/
 │   ├── booking/
 │   ├── group-class/
 │   ├── pt/
 │   ├── private-room/
 │   ├── inventory/
 │   ├── pantry/
 │   └── equipment/
 └── pages/               # route-level screens if using page folder
```

## TypeScript Rule
- Do not use plain JavaScript for main source files.
- API request/response models must be typed.
- Avoid `any` unless there is a clear reason.
- Use domain-specific types/enums for statuses.

## API Integration
- Keep API client functions separated from UI components.
- UI components should not construct raw URLs everywhere.
- Centralize auth token handling.
- Centralize error handling for business errors.

## UI/UX Baseline
Admin screens should prioritize operational speed:
- Search member by phone/CCCD/member code.
- Quick check-in support.
- Booking calendar views.
- Payment status visibility.
- Branch filter everywhere relevant.
- Clear status badges.

## Documentation Rule
When choosing frontend libraries such as UI kit, router, query/caching, state management, or form library, update this file and create an ADR if the decision is important.
