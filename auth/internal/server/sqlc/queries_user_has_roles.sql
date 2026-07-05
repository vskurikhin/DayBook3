-- name: GetRolesForUserName :one
SELECT user_name, enabled, array_agg(role) AS roles
FROM user_has_roles
WHERE enabled AND user_name = $1
GROUP BY user_name, enabled LIMIT 1;

-- name: ListUserHasRolesByRole :many
SELECT * FROM user_has_roles
WHERE role = $1;

-- name: ListUserHasRolesByUserName :many
SELECT * FROM user_has_roles
WHERE user_name = $1;

-- name: ListUserHasRoles :many
SELECT * FROM user_has_roles
ORDER BY user_name;

-- name: CreateUserHasRoles :one
INSERT INTO user_has_roles (
    user_name, role, enabled, local_change
) VALUES (
  $1, $2, true, true
)
RETURNING *;

-- name: UpdateUserHasRoles :exec
UPDATE user_has_roles
  set enabled = $2
WHERE user_name = $1;

-- name: DeleteUserHasRolesBy :exec
DELETE FROM user_has_roles
WHERE user_name = $1;

-- name: DeleteUserHasRolesByID :exec
DELETE FROM user_has_roles
WHERE id = $1;
