SELECT EXISTS (
    SELECT 1
    FROM membership.membership
    WHERE member_id = :memberId
      AND status = 'ACTIVE'
      AND (effective_from IS NULL OR effective_from <= now())
      AND (effective_to IS NULL OR effective_to >= now())
) AS exists
