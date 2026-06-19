INSERT INTO branch.branch_branch (
    code,
    name,
    address,
    district,
    city,
    phone,
    open_24h,
    status
) VALUES (
    :code,
    :name,
    :address,
    :district,
    :city,
    :phone,
    :open24h,
    :status
)
RETURNING id
