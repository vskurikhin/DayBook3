package db

import (
	"sync"
	"testing"

	"github.com/jackc/pgx/v5/pgxpool"
)

func TestIsNotEqual_NilA(t *testing.T) {
	p := &PgxPool{
		pgxPool: &pgxpool.Pool{},
		mu:      sync.RWMutex{},
	}

	if p.IsNotEqual(nil) {
		t.Fatal("expected false when a is nil")
	}
}

func TestIsNotEqual_NilP(t *testing.T) {
	a := &pgxpool.Pool{}

	if (*PgxPool)(nil).IsNotEqual(a) {
		t.Fatal("expected false when p is nil")
	}
}

func TestIsNotEqual_InvalidPoolType(t *testing.T) {
	a := &pgxpool.Pool{}

	p := &PgxPool{
		pgxPool: &mockPool{}, // not *pgxpool.Pool
		mu:      sync.RWMutex{},
	}

	if p.IsNotEqual(a) {
		t.Fatal("expected false when pool type assertion fails")
	}
}

func TestIsNotEqual_SamePool(t *testing.T) {
	a := &pgxpool.Pool{}

	p := &PgxPool{
		pgxPool: a,
		mu:      sync.RWMutex{},
	}

	if p.IsNotEqual(a) {
		t.Fatal("expected false when pools are equal")
	}
}

func TestIsNotEqual_DifferentPools(t *testing.T) {
	a := &pgxpool.Pool{}
	b := &pgxpool.Pool{}

	p := &PgxPool{
		pgxPool: b,
		mu:      sync.RWMutex{},
	}

	if !p.IsNotEqual(a) {
		t.Fatal("expected true when pools are different")
	}
}
