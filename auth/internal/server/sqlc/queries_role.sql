-- name: GetRole :one
SELECT * FROM role
WHERE role = $1 LIMIT 1;

-- name: ListRoles :many
SELECT * FROM role
ORDER BY role;

-- name: CreateRole :one
INSERT INTO role (
    role, description, user_name, enabled, local_change
) VALUES (
  $1, $2, $3, true, true
)
RETURNING *;

-- name: UpdateRole :exec
UPDATE role
  set description = $2,
  enabled = $3
WHERE role = $1;

-- name: DeleteRoleByName :exec
DELETE FROM role
WHERE role = $1;

-- name: DeleteRoleByID :exec
DELETE FROM role
WHERE id = $1;
