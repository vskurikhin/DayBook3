// Package db provides a database abstraction layer for working with a
// PostgreSQL connection pool using the pgx driver.
//
// The package is responsible for initializing, managing, and providing
// access to a shared pgx-based connection pool used by the DayBook
// authentication server.
//
// # Overview
//
// The db package encapsulates configuration and lifecycle management
// of a PostgreSQL connection pool built on top of github.com/jackc/pgx/v5/pgxpool.
// It exposes a minimal database interface that supports common operations:
//
//   - Begin    — start a database transaction
//   - Exec     — execute SQL statements
//   - Query    — execute queries returning multiple rows
//   - QueryRow — execute queries returning a single row
//
// The package manages a singleton instance of the database pool that can
// be safely shared across multiple goroutines.
//
// # Architecture
//
// The main components of this package are:
//
//	DB interface
//	  Defines a minimal abstraction over database operations used by
//	  the application. It mirrors the common methods provided by pgx.
//
//	PgxPool
//	  A wrapper around pgxpool.Pool implementing the DB interface.
//	  It provides thread-safe access to the connection pool.
//
//	Connection Builder
//	  A fluent connection string builder is used to construct a PostgreSQL
//	  connection string from application configuration.
//
//	Global Pool
//	  A singleton instance of the connection pool is created using sync.Once
//	  and stored in the package-level variable pgxPool.
//
// # Initialization
//
// The database pool is initialized using NewDB:
//
//	db, err := db.NewDB(ctx, cfg)
//	if err != nil {
//	    log.Fatal(err)
//	}
//
// This function:
//
//  1. Builds a PostgreSQL connection string using configuration values.
//  2. Parses the connection string into pgxpool.Config.
//  3. Creates a new pgxpool.Pool instance.
//  4. Stores the pool in a singleton wrapper (PgxPool).
//
// Only the first call to NewDB creates the pool. Subsequent calls return
// the already initialized instance.
//
// # Accessing the Pool
//
// The shared pool can be accessed using GetDB:
//
//	db, err := db.GetDB()
//	if err != nil {
//	    log.Fatal(err)
//	}
//
// If the pool has not been initialized, GetDB returns ErrDBPoolIsNil.
//
// # Automatic Pool Reload (SIGHUP)
//
// The package supports dynamic reloading of the database pool when the
// process receives a SIGHUP signal.
//
// When SIGHUP is received:
//
//  1. A new connection pool is created using the current configuration.
//  2. If initialization succeeds, the existing pool reference is replaced.
//  3. If initialization fails, the previous pool remains active.
//
// This mechanism allows runtime configuration reloads without restarting
// the application.
//
// # Concurrency
//
// Access to the internal pgx pool is protected using a mutex inside PgxPool.
// This ensures safe concurrent access when the pool reference is updated
// during reload operations.
//
// The pool itself (pgxpool.Pool) is designed for concurrent usage and
// manages internal connection pooling.
//
// # Configuration
//
// Database settings are obtained from the server configuration package:
//
//	github.com/vskurikhin/DayBook3/auth/v2/internal/server/config
//
// Configuration values include:
//
//   - DBUser
//   - DBPassword
//   - DBHost
//   - DBPort
//   - DBName
//   - DBOptions
//   - connection pool parameters
//
// These values are used to construct the PostgreSQL connection string.
//
// # Logging
//
// The package uses structured logging via log/slog. Debug logs include
// connection information and pool initialization events when debug mode
// is enabled in the configuration.
//
// # Error Handling
//
// Errors may occur during:
//
//   - connection string construction
//   - configuration parsing
//   - connection pool initialization
//
// Such errors are returned to the caller of NewDB.
//
// The package also defines:
//
//	ErrDBPoolIsNil
//
// which is returned when attempting to access the database pool before
// it has been initialized.
//
// # Testing
//
// The package is designed to support unit testing by allowing the
// pgxpool constructor to be overridden via the newWithConfig variable.
// This enables injecting mock pools during tests without connecting
// to a real database.
//
// # Example
//
// Basic usage:
//
//	ctx := context.Background()
//
//	db, err := db.NewDB(ctx, cfg)
//	if err != nil {
//	    log.Fatal(err)
//	}
//
//	rows, err := db.Query(ctx, "SELECT id, name FROM users")
//	if err != nil {
//	    log.Fatal(err)
//	}
//	defer rows.Close()
//
// # Design Goals
//
// The db package aims to:
//
//   - provide a thin abstraction over pgxpool
//   - centralize database initialization
//   - support runtime pool reloading
//   - simplify testing of database-dependent components
//   - maintain high concurrency safety
//
// It intentionally avoids reimplementing database logic already provided
// by pgx and instead focuses on lifecycle management and integration
// with application configuration.
package db

import (
	"context"
	"errors"
	"fmt"
	"log/slog"
	"os"
	"os/signal"
	"strconv"
	"sync"
	"syscall"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/env"
)

type DB interface {
	Acquire(ctx context.Context) (c *pgxpool.Conn, err error)
	Begin(ctx context.Context) (pgx.Tx, error)
	Exec(ctx context.Context, sql string, arguments ...interface{}) (pgconn.CommandTag, error)
	Query(ctx context.Context, sql string, optionsAndArgs ...interface{}) (pgx.Rows, error)
	QueryRow(ctx context.Context, sql string, optionsAndArgs ...interface{}) pgx.Row
}

//go:generate mockgen -destination=mock_pgx_conn_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services PgxConn
type PgxConn interface {
	QueryRow(ctx context.Context, sql string, args ...any) pgx.Row
	Exec(ctx context.Context, sql string, args ...any) (pgconn.CommandTag, error)
	Release()
}

var _ DB = (*PgxPool)(nil)

var (
	ErrDBPoolIsNil = errors.New("db pool is nil")
	pgxPool        *PgxPool
	once           = new(sync.Once)
	newWithConfig  = func(ctx context.Context, config *pgxpool.Config) (*pgxpool.Pool, error) {
		return pgxpool.NewWithConfig(ctx, config)
	}
)

// NewDB initializes and returns a singleton instance of PgxPool.
//
// The function creates a PostgreSQL connection pool using configuration
// values provided by cfg. The pool is created only once using sync.Once;
// subsequent calls return the already initialized instance.
//
// During initialization the function:
//
//   - Builds a PostgreSQL connection string from configuration values
//   - Parses the connection string into pgxpool.Config
//   - Creates a pgx connection pool
//   - Stores the pool in a shared PgxPool instance
//
// A background goroutine is also started to listen for SIGHUP signals.
// When such a signal is received, the database pool is reloaded using
// the current configuration.
//
// If pool creation fails, an error is returned and the pool is not initialized.
func NewDB(ctx context.Context, cfg config.Config, env env.Environments) (*PgxPool, error) {
	var pgxp *pgxpool.Pool
	if pgxPool == nil || pgxPool.pgxPool == nil {
		var err error
		pgxp, err = newDB(ctx, cfg)
		if err != nil {
			return nil, err
		}
	}
	once.Do(func() {
		pgxPool = &PgxPool{}
		pgxPool.mu.Lock()
		defer pgxPool.mu.Unlock()
		pgxPool.pgxPool = pgxp
		go loopSigHup(ctx, cfg, env)
	})
	return pgxPool, nil
}

// GetDB provider a singleton instance of PgxPool.
func GetDB() (*PgxPool, error) {
	if pgxPool == nil {
		return nil, ErrDBPoolIsNil
	}
	pgxPool.mu.RLock()
	defer pgxPool.mu.RUnlock()
	return pgxPool, nil
}

func newDB(ctx context.Context, cfg config.Config) (*pgxpool.Pool, error) {
	builder := ConstructConnectionStringBuilder(
		WithDBUser(cfg.Values().DBUser),
		WithDBPassword(cfg.Values().DBPassword),
		WithDBHost(cfg.Values().DBHost),
		WithDBPort(strconv.Itoa(int(cfg.Values().DBPort))),
		WithDBName(cfg.Values().DBName),
	).WithDBOptions(cfg.Values().DBOptions).
		WithDBPoolMaxConns(cfg.Values().DBPoolMaxConns).
		WithDBPoolMinConns(cfg.Values().DBPoolMinConns).
		WithDBPoolMaxConnLifeTime(cfg.Values().DBPoolMaxConnLifeTime).
		WithDBPoolMaxConnIdleTime(cfg.Values().DBPoolMaxConnIdleTime).
		WithDBPoolHealthCheckPeriod(cfg.Values().DBPoolHealthCheckPeriod)
	connString, errBuild := builder.Build()
	if errBuild != nil {
		return nil, errBuild
	}
	pgxPoolConfig, errParseConfig := pgxpool.ParseConfig(connString)
	if errParseConfig != nil {
		return nil, errParseConfig
	}
	if cfg.Values().Debug {
		slog.Debug("Connecting to database using pgx DB pool", slog.String("URL", connString))
	}
	pgxp, err := newWithConfig(ctx, pgxPoolConfig)
	if err != nil {
		return nil, err
	}
	return pgxp, nil
}

func loopSigHup(ctx context.Context, cfg config.Config, environments env.Environments) {
	sigHup := make(chan os.Signal, 1)
	signal.Notify(sigHup, syscall.SIGHUP)
	for {
		select {
		case <-ctx.Done():
			return
		case <-sigHup:
			slog.Info("Pgx DB pool, reload signal received")
			time.Sleep(environments.Values().PgxPoolReloadTimeout)
			pgxp, err := newDB(ctx, cfg)
			if err != nil {
				slog.Info("Pgx DB pool, reload fail", slog.String("error", err.Error()))
				continue
			}
			if pgxPool.IsNotEqual(pgxp) {
				slog.Info("Pgx DB pool, reloading...")
				slog.Debug(
					"Reloading pgx DB pool",
					slog.String("old", fmt.Sprintf("%p", pgxPool.pgxPool)),
					slog.String("new", fmt.Sprintf("%p", pgxp)),
				)
				oldPgxPool := pgxPool.pgxPool
				pgxPool.mu.Lock()
				pgxPool.pgxPool = pgxp
				pgxPool.mu.Unlock()
				go func() {
					time.Sleep(environments.Values().OldPgxPoolCloseTimeout)
					oldPgxPool.Close()
				}()
				slog.Info("Pgx DB pool, reload")
			}
		}
	}
}
