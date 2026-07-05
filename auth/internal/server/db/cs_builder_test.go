package db

import (
	"errors"
	"strings"
	"testing"
	"time"
)

func baseBuilder() ConnectionStringBuilder {
	return ConstructConnectionStringBuilder(
		WithDBUser("user"),
		WithDBPassword("pass"),
		WithDBHost("localhost"),
		WithDBPort("5432"),
		WithDBName("testdb"),
	)
}

func TestBuild_Success(t *testing.T) {
	b := baseBuilder()

	conn, err := b.Build()
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	expected := "postgres://user:pass@localhost:5432/testdb"

	if conn != expected {
		t.Fatalf("expected %s got %s", expected, conn)
	}
}

func TestBuild_WithTailOptions(t *testing.T) {
	b := baseBuilder()

	b = b.
		WithDBOptions("?sslmode=disable").
		WithDBPoolMaxConns(10)

	conn, err := b.Build()
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	if !strings.Contains(conn, "pool_max_conns=10") {
		t.Fatalf("expected pool_max_conns option in %s", conn)
	}
}

func TestBuild_ErrorMissingUser(t *testing.T) {
	b := ConstructConnectionStringBuilder(
		WithDBPassword("pass"),
		WithDBHost("localhost"),
		WithDBPort("5432"),
		WithDBName("testdb"),
	)

	_, err := b.Build()

	if !errors.Is(err, ErrNotReady) {
		t.Fatalf("expected ErrNotReady got %v", err)
	}
}

func TestBuild_ErrorMissingPassword(t *testing.T) {
	b := ConstructConnectionStringBuilder(
		WithDBUser("user"),
		WithDBHost("localhost"),
		WithDBPort("5432"),
		WithDBName("testdb"),
	)

	_, err := b.Build()

	if !errors.Is(err, ErrNotReady) {
		t.Fatalf("expected ErrNotReady got %v", err)
	}
}

func TestBuild_ErrorMissingHost(t *testing.T) {
	b := ConstructConnectionStringBuilder(
		WithDBUser("user"),
		WithDBPassword("pass"),
		WithDBPort("5432"),
		WithDBName("testdb"),
	)

	_, err := b.Build()

	if !errors.Is(err, ErrNotReady) {
		t.Fatalf("expected ErrNotReady got %v", err)
	}
}

func TestBuild_ErrorMissingPort(t *testing.T) {
	b := ConstructConnectionStringBuilder(
		WithDBUser("user"),
		WithDBPassword("pass"),
		WithDBHost("localhost"),
		WithDBName("testdb"),
	)

	_, err := b.Build()

	if !errors.Is(err, ErrNotReady) {
		t.Fatalf("expected ErrNotReady got %v", err)
	}
}

func TestBuild_ErrorMissingDBName(t *testing.T) {
	b := ConstructConnectionStringBuilder(
		WithDBUser("user"),
		WithDBPassword("pass"),
		WithDBHost("localhost"),
		WithDBPort("5432"),
	)

	_, err := b.Build()

	if !errors.Is(err, ErrNotReady) {
		t.Fatalf("expected ErrNotReady got %v", err)
	}
}

func TestWithDBPoolMaxConns(t *testing.T) {
	b := baseBuilder()

	b = b.WithDBPoolMaxConns(20)

	if !strings.Contains(b.tail, "pool_max_conns=20") {
		t.Fatalf("expected pool_max_conns in tail got %s", b.tail)
	}
}

func TestWithDBPoolMinConns(t *testing.T) {
	b := baseBuilder()

	b = b.WithDBPoolMinConns(5)

	if !strings.Contains(b.tail, "pool_min_conns=5") {
		t.Fatalf("expected pool_min_conns in tail got %s", b.tail)
	}
}

func TestWithDBPoolMaxConnLifeTime(t *testing.T) {
	b := baseBuilder()

	b = b.WithDBPoolMaxConnLifeTime(5 * time.Second)

	if !strings.Contains(b.tail, "pool_max_conn_lifetime=") {
		t.Fatalf("expected pool_max_conn_lifetime option")
	}
}

func TestWithDBPoolMaxConnIdleTime(t *testing.T) {
	b := baseBuilder()

	b = b.WithDBPoolMaxConnIdleTime(3 * time.Second)

	if !strings.Contains(b.tail, "pool_max_conn_idle_time=") {
		t.Fatalf("expected pool_max_conn_idle_time option")
	}
}

func TestWithDBPoolHealthCheckPeriod(t *testing.T) {
	b := baseBuilder()

	b = b.WithDBPoolHealthCheckPeriod(2 * time.Second)

	if !strings.Contains(b.tail, "pool_health_check_period=") {
		t.Fatalf("expected pool_health_check_period option")
	}
}

func TestWithDBOptions_EmptyIgnored(t *testing.T) {
	b := baseBuilder()

	b2 := b.WithDBOptions("")

	if b2.tail != "" {
		t.Fatalf("expected empty tail got %s", b2.tail)
	}
}

func TestConstructConnectionStringBuilder(t *testing.T) {
	b := ConstructConnectionStringBuilder(
		WithDBHost("127.0.0.1"),
		WithDBUser("admin"),
	)

	if b.dbHost != "127.0.0.1" {
		t.Fatalf("expected host set")
	}

	if b.dbUser != "admin" {
		t.Fatalf("expected user set")
	}
}
