-- name: GetSession :one
SELECT * FROM session
WHERE iss = $1 AND jti = $2 AND sub = $3 AND enabled
LIMIT 1;

-- name: ListSessions :many
SELECT * FROM session
ORDER BY user_name;

-- name: CreateSession :one
INSERT INTO session (
    iss, jti, sub, user_name, roles, valid_time, enabled, local_change
) VALUES (
  $1, $2, $3, $4, $5, $6, true, true
)
RETURNING *;

-- name: UpdateSession :exec
UPDATE session
  SET roles = $4,
      valid_time = $5,
      enabled = $6
WHERE iss = $1 AND jti = $2 AND sub = $3;

-- name: DeleteSession :exec
DELETE FROM session
WHERE iss = $1 AND jti = $2 AND sub = $3;

-- name: DeleteSessionWhereValidTimeLessThanNow :exec
DELETE FROM session WHERE valid_time < now();
