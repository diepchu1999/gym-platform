INSERT INTO staff.staff_staff (
    employee_code,
    full_name,
    phone,
    email,
    status
) VALUES (
    :employeeCode,
    :fullName,
    :phone,
    :email,
    :status
)
RETURNING id
