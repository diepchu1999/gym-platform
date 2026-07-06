UPDATE membership.membership
SET status = :status
WHERE code = :code
  AND status = :expectedStatus
