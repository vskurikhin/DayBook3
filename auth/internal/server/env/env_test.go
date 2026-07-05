package env

import (
	"errors"
	"testing"
	"time"

	"github.com/caarlos0/env/v11"
)

func TestEnvironmentsLoad_DefaultValue(t *testing.T) {
	t.Setenv("SERVER_TIMEOUT", "")

	environments, err := EnvironmentsLoad()
	if err != nil {
		t.Fatal(err)
	}
	values := environments.Values()

	expected := 10 * time.Second
	if values.Timeout != expected {
		t.Errorf("expected timeout %v, got %v", expected, values.Timeout)
	}
}

func TestEnvironmentsLoad_FromEnv(t *testing.T) {
	t.Setenv("AUTH_SERVER_TIMEOUT", "30s")

	environments, err := EnvironmentsLoad()
	if err != nil {
		t.Fatal(err)
	}
	values := environments.Values()

	expected := 30 * time.Second
	if values.Timeout != expected {
		t.Errorf("expected timeout %v, got %v", expected, values.Timeout)
	}
}

func TestValues_Method(t *testing.T) {
	cfg := Config{
		values: Values{
			Timeout: 15 * time.Second,
		},
	}

	values := cfg.Values()

	if values.Timeout != 15*time.Second {
		t.Errorf("expected timeout %v, got %v", 15*time.Second, values.Timeout)
	}
}

func TestEnvironmentsLoad_InvalidDuration(t *testing.T) {
	t.Setenv("AUTH_SERVER_TIMEOUT", "invalid")
	_, err := EnvironmentsLoad()
	if err == nil {
		t.Fatal("expected error")
	}
	var aggregateError env.AggregateError
	if !errors.As(err, &aggregateError) {
		t.Fatal("expected env.AggregateError")
	}
}
