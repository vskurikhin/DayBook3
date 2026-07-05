package db

import (
	"context"
	"errors"
	"os"
	"os/signal"
	"syscall"
	"testing"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/stretchr/testify/assert"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/env"
)

func TestLoopSigHup_SignalReload(t *testing.T) {
	sig := make(chan os.Signal, 1)

	signal.Notify(sig, syscall.SIGHUP)

	resetSingleton()

	original := newWithConfig
	defer func() { newWithConfig = original }()

	newWithConfig = func(ctx context.Context, cfg *pgxpool.Config) (*pgxpool.Pool, error) {
		return &pgxpool.Pool{}, nil
	}

	db, err := NewDB(context.Background(), validConfig(), newMockEnvironments(5*time.Second, true))
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	before := db.pgxPool

	go func() {
		time.Sleep(50 * time.Millisecond)
		_ = syscall.Kill(syscall.Getpid(), syscall.SIGHUP)
		time.Sleep((env.PgxPoolReloadTimeout + 100) * time.Millisecond)
	}()

	select {
	case <-sig:
		// OK
	case <-time.After(time.Second):
		t.Fatal("signal not received")
	}
	time.Sleep(env.PgxPoolReloadTimeout + 200*time.Millisecond)
	if &db.pgxPool == &before {
		t.Fatal("db returned before before")
	}
	assert.Equal(t, db.pgxPool, before)
}

var ErrTest = errors.New("test error")

func TestLoopSigHup_SignalReload_WithError(t *testing.T) {
	sig := make(chan os.Signal, 1)

	signal.Notify(sig, syscall.SIGHUP)

	resetSingleton()

	original := newWithConfig
	defer func() { newWithConfig = original }()

	newWithConfig = func(ctx context.Context, cfg *pgxpool.Config) (*pgxpool.Pool, error) {
		return &pgxpool.Pool{}, nil
	}

	db, err := NewDB(context.Background(), validConfig(), newMockEnvironments(5*time.Second, true))
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	before := db.pgxPool

	newWithConfig = func(ctx context.Context, cfg *pgxpool.Config) (*pgxpool.Pool, error) {
		return nil, ErrTest
	}

	go func() {
		time.Sleep(50 * time.Millisecond)
		_ = syscall.Kill(syscall.Getpid(), syscall.SIGHUP)
		time.Sleep((env.PgxPoolReloadTimeout + 100) * time.Millisecond)
	}()

	select {
	case <-sig:
		// OK
	case <-time.After(time.Second):
		t.Fatal("signal not received")
	}
	time.Sleep(env.PgxPoolReloadTimeout + 200*time.Millisecond)
	if &db.pgxPool == &before {
		t.Fatal("db returned before before")
	}
	assert.Equal(t, db.pgxPool, before)
}

func TestLoopSigHup_ContextDone(t *testing.T) {
	// Запускаем loopSigHup с контекстом, который сразу отменяется
	ctx, cancel := context.WithCancel(context.Background())
	cancel() // сразу отменяем

	resetSingleton()

	original := newWithConfig
	defer func() { newWithConfig = original }()

	newWithConfig = func(ctx context.Context, cfg *pgxpool.Config) (*pgxpool.Pool, error) {
		return &pgxpool.Pool{}, nil
	}

	// Функция должна завершиться сразу, не блокируя тест
	_, err := NewDB(ctx, validConfig(), nil)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
}
