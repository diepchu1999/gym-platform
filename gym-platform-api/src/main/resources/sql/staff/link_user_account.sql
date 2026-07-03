UPDATE staff.staff_staff
SET user_account_id = :userAccountId
WHERE id = :staffId
  AND (user_account_id IS NULL OR user_account_id = :userAccountId)
