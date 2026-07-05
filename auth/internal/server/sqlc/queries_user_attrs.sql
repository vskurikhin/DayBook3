-- name: GetUserAttrs :one
SELECT * FROM user_attrs
WHERE user_name = $1 LIMIT 1;

-- name: ListUserAttrs :many
SELECT * FROM user_attrs
ORDER BY user_name;

-- name: CreateUserAttrs :one
INSERT INTO user_attrs (
    user_name, attrs, name, enabled, local_change
) VALUES (
  $1, $2, $3, true, true
)
RETURNING *;

-- name: UpdateUserAttrs :exec
UPDATE user_attrs
  SET attrs = $2,
      name = $3,
      enabled = $4
WHERE user_name = $1;

-- name: DeleteUserAttrs :exec
DELETE FROM user_attrs
WHERE user_name = $1;