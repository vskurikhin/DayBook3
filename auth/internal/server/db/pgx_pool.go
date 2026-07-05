package db

import (
	"context"
	"sync"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"
)

//go:generate mockgen -destination=pgx_pool_mock_test.go -package=db github.com/vskurikhin/DayBook3/auth/v2/internal/server/db Pool
type Pool interface {
	Acquire(ctx context.Context) (c *pgxpool.Conn, err error)
	Begin(ctx context.Context) (pgx.Tx, error)
	Close()
	Exec(ctx context.Context, sql string, arguments ...any) (pgconn.CommandTag, error)
	Ping(ctx context.Context) error
	Query(ctx context.Context, sql string, args ...any) (pgx.Rows, error)
	QueryRow(ctx context.Context, sql string, args ...any) pgx.Row
	Stat() *pgxpool.Stat
}

type PgxPool struct {
	pgxPool Pool
	mu      sync.RWMutex
}

// Acquire returns a connection from the underlying pgx connection pool.
//
// It safely reads the current pool instance using a read lock and delegates
// the call to the wrapped Pool implementation.
//
// The returned connection must be released by the caller.
// An error is returned if acquiring a connection fails.
func (p *PgxPool) Acquire(ctx context.Context) (c *pgxpool.Conn, err error) {
	p.mu.RLock()
	pool := p.pgxPool
	p.mu.RUnlock()
	return pool.Acquire(ctx)

}

// Begin starts a new database transaction using a connection acquired
// from the underlying pgx connection pool.
//
// The method acquires a connection from the pool and calls Begin on it.
// If acquiring the connection fails, an error is returned.
func (p *PgxPool) Begin(ctx context.Context) (pgx.Tx, error) {
	p.mu.RLock()
	pool := p.pgxPool
	p.mu.RUnlock()
	return pool.Begin(ctx)
}

// Exec executes the given SQL statement using a connection acquired
// from the underlying pgx connection pool.
//
// It returns a CommandTag containing execution metadata (such as the
// number of affected rows). If acquiring a connection or executing the
// statement fails, an error is returned.
func (p *PgxPool) Exec(ctx context.Context, sql string, arguments ...interface{}) (pgconn.CommandTag, error) {
	p.mu.RLock()
	pool := p.pgxPool
	p.mu.RUnlock()
	return pool.Exec(ctx, sql, arguments...)
}

// Query executes a SQL query using a connection acquired from the
// underlying pgx connection pool and returns the resulting rows.
//
// The caller is responsible for closing the returned pgx.Rows.
// An error is returned if the connection cannot be acquired or
// if the query execution fails.
func (p *PgxPool) Query(ctx context.Context, sql string, optionsAndArgs ...interface{}) (pgx.Rows, error) {
	p.mu.RLock()
	pool := p.pgxPool
	p.mu.RUnlock()
	return pool.Query(ctx, sql, optionsAndArgs...)
}

// QueryRow executes a SQL query expected to return at most one row
// using a connection acquired from the underlying pgx connection pool.
//
// It returns a pgx.Row that can be scanned by the caller.
// An error is returned if acquiring the connection from the pool fails.
func (p *PgxPool) QueryRow(ctx context.Context, sql string, optionsAndArgs ...interface{}) pgx.Row {
	p.mu.RLock()
	pool := p.pgxPool
	p.mu.RUnlock()
	return pool.QueryRow(ctx, sql, optionsAndArgs...)
}

// IsNotEqual reports whether the provided pgxpool.Pool instance differs
// from the pool stored inside the given PgxPool.
//
// It returns false if either argument is nil or if the internal pool
// cannot be asserted to *pgxpool.Pool.
func (p *PgxPool) IsNotEqual(a *pgxpool.Pool) bool {
	if a == nil || p == nil {
		return false
	}
	p.mu.RLock()
	defer p.mu.RUnlock()
	b, ok := p.pgxPool.(*pgxpool.Pool)
	if !ok {
		return false
	}
	if a != b {
		return true
	}
	return false
}
