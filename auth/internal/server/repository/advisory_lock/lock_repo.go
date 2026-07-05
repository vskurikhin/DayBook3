package advisory_lock

import (
	"context"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
)

//go:generate mockgen -destination=mock_pgx_conn_test.go -package=advisory_lock github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/advisory_lock PgxConn
type PgxConn interface {
	QueryRow(ctx context.Context, sql string, args ...any) pgx.Row
	Exec(ctx context.Context, sql string, args ...any) (pgconn.CommandTag, error)
}

//go:generate mockgen -destination=mock_row_test.go -package=advisory_lock github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/advisory_lock Row
type Row interface {
	Scan(dest ...any) error
}

func AcquireLock(ctx context.Context, conn PgxConn, key int64) (bool, error) {
	var ok bool
	err := conn.QueryRow(ctx, "SELECT pg_try_advisory_lock($1)", key).Scan(&ok)
	return ok, err
}

func ReleaseLock(ctx context.Context, conn PgxConn, key int64) error {
	_, err := conn.Exec(ctx, "SELECT pg_advisory_unlock($1)", key)
	return err
}
