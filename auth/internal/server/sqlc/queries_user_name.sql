-- name: GetUserName :one
SELECT * FROM user_name
WHERE user_name = $1 LIMIT 1;

-- name: ListUserNames :many
SELECT * FROM user_name
ORDER BY user_name;

-- name: CreateUserName :one
INSERT INTO user_name (
    user_name, password, enabled, local_change
) VALUES (
  $1, $2, true, true
)
RETURNING *;

-- name: UpdateUserName :exec
UPDATE user_name
  SET password = $2,
      enabled = $3
WHERE user_name = $1;

-- name: DeleteUserNameByName :exec
DELETE FROM user_name
WHERE user_name = $1;

-- name: DeleteUserNameByID :exec
DELETE FROM user_name
WHERE id = $1;
