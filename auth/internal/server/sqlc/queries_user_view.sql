-- name: GetUserName :one
SELECT * FROM user_view
WHERE user_name = $1 LIMIT 1;

-- name: ListUserNames :many
SELECT * FROM user_view
ORDER BY user_name;
