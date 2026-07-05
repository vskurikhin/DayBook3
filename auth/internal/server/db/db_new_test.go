package db

import (
	"context"
	"errors"
	"sync"
	"testing"
	"time"

	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/env"
)

type mockConfig struct {
	values config.Values
}

func (m mockConfig) Values() config.Values {
	return m.values
}

func (t mockConfig) JWThs256SignKey(_ string) {}

func validConfig() config.Config {
	return mockConfig{
		values: config.Values{
			DBUser:                  "user",
			DBPassword:              "pass",
			DBHost:                  "localhost",
			DBPort:                  5432,
			DBName:                  "testdb",
			DBOptions:               "?sslmode=disable",
			DBPoolMaxConns:          10,
			DBPoolMinConns:          1,
			DBPoolMaxConnLifeTime:   time.Minute,
			DBPoolMaxConnIdleTime:   time.Minute,
			DBPoolHealthCheckPeriod: time.Minute,
			Debug:                   false,
		},
	}
}

//
// ---- Mock Environments ----
//

type mockEnv struct {
	values env.Values
}

func (m mockEnv) Values() env.Values {
	return m.values
}

func newMockEnvironments(timeout time.Duration, debugPprof bool) env.Environments {
	return &mockEnv{
		values: env.Values{
			Timeout:                timeout,
			DebugPprof:             debugPprof,
			OldPgxPoolCloseTimeout: 11 * time.Second,
			PgxPoolReloadTimeout:   100 * time.Millisecond,
		},
	}
}

func resetSingleton() {
	pgxPool = nil
	once = new(sync.Once)
}

func TestNewDB_Success(t *testing.T) {
	resetSingleton()

	original := newWithConfig
	defer func() { newWithConfig = original }()

	newWithConfig = func(ctx context.Context, cfg *pgxpool.Config) (*pgxpool.Pool, error) {
		return &pgxpool.Pool{}, nil
	}

	db, err := NewDB(context.Background(), validConfig(), nil)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	if db == nil {
		t.Fatal("expected db instance")
	}

	if db.pgxPool == nil {
		t.Fatal("expected pgxPool initialized")
	}
}

func TestNewDB_BuildError(t *testing.T) {
	resetSingleton()

	cfg := mockConfig{
		values: config.Values{
			DBUser: "", // вызывает Build() error
		},
	}

	_, err := NewDB(context.Background(), cfg, nil)

	if err == nil {
		t.Fatal("expected error")
	}
}

func TestNewDB_NewWithConfigError(t *testing.T) {
	resetSingleton()

	original := newWithConfig
	defer func() { newWithConfig = original }()

	newWithConfig = func(ctx context.Context, cfg *pgxpool.Config) (*pgxpool.Pool, error) {
		return nil, errors.New("pool error")
	}

	_, err := NewDB(context.Background(), validConfig(), nil)

	if err == nil {
		t.Fatal("expected error")
	}
}

func TestNewDB_Singleton(t *testing.T) {
	resetSingleton()

	original := newWithConfig
	defer func() { newWithConfig = original }()

	newWithConfig = func(ctx context.Context, cfg *pgxpool.Config) (*pgxpool.Pool, error) {
		if pgxPool == nil || pgxPool.pgxPool == nil {
			return &pgxpool.Pool{}, nil
		}
		return nil, nil
	}

	cfg := validConfig()

	db1, err := NewDB(context.Background(), cfg, newMockEnvironments(5*time.Second, true))
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	db2, err := NewDB(context.Background(), cfg, newMockEnvironments(5*time.Second, true))
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	if db1 != db2 {
		t.Fatal("expected singleton instance")
	}

	if pgxPool == nil {
		t.Fatal("expected pgxPool initialized")
	}
	if pgxPool.pgxPool == nil {
		t.Fatal("expected pgxPool.pgxPool initialized")
	}
}

func TestNewDB_DebugMode(t *testing.T) {
	resetSingleton()

	original := newWithConfig
	defer func() { newWithConfig = original }()

	newWithConfig = func(ctx context.Context, cfg *pgxpool.Config) (*pgxpool.Pool, error) {
		return &pgxpool.Pool{}, nil
	}

	cfg := mockConfig{
		values: validConfig().Values(),
	}

	cfg.values.Debug = true

	db, err := NewDB(context.Background(), cfg, nil)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	if db == nil {
		t.Fatal("expected db instance")
	}
}

func invalidConfig() config.Config {
	return mockConfig{
		values: config.Values{
			DBUser:                  "user",
			DBPassword:              "pass",
			DBHost:                  "localhost",
			DBPort:                  5432,
			DBName:                  "testdb",
			DBOptions:               "description_cache_capacity=none",
			DBPoolMaxConns:          10,
			DBPoolMinConns:          1,
			DBPoolMaxConnLifeTime:   time.Minute,
			DBPoolMaxConnIdleTime:   time.Minute,
			DBPoolHealthCheckPeriod: time.Minute,
			Debug:                   false,
		},
	}
}

func TestNewDB_NewWithConfigParseConfigError(t *testing.T) {
	resetSingleton()

	_, err := NewDB(context.Background(), invalidConfig(), nil)

	var e *pgconn.ParseConfigError
	if !errors.As(err, &e) {
		t.Fatal("expected pgconn.ParseConfigError")
	}
}

func TestGetDB_PoolIsNil(t *testing.T) {
	resetSingleton()
	db, err := GetDB()

	if !errors.Is(err, ErrDBPoolIsNil) {
		t.Fatalf("expected ErrDBPoolIsNil got %v", err)
	}

	if db != nil {
		t.Fatal("expected nil db")
	}
}

func TestGetDB_Success(t *testing.T) {
	resetSingleton()
	expected := &PgxPool{
		mu: sync.RWMutex{},
	}

	pgxPool = expected

	db, err := GetDB()

	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	if db != expected {
		t.Fatal("expected returned db to match global pgxPool")
	}
}
