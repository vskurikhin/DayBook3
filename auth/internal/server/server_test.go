package server

import (
	"context"
	"net/http"
	"testing"
	"time"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/env"
)

//
// ---- Mock Environments ----
//

type mockEnv struct {
	values env.Values
}

func (m mockEnv) Values() env.Values {
	return m.values
}

var environments = mockEnv{
	values: env.Values{
		Timeout: 5 * time.Second,
	},
}

// Тестируем создание нового AuthServer
func TestNewAuthServer(t *testing.T) {
	cfg := config.Values{
		Address: "127.0.0.1:0", // Порт 0 для случайного свободного порта
		Debug:   true,
	}
	handler := http.NewServeMux()

	srv, err := NewAuthServer(newTestConfig(), environments, handler)
	if err != nil {
		t.Errorf("NewAuthServer() error = %v", err)
	}

	if srv.cfg.Values().Address != cfg.Address {
		t.Errorf("expected address %s, got %s", cfg.Address, srv.cfg.Values().Address)
	}

	if srv.handler != handler {
		t.Errorf("expected handler %v, got %v", handler, srv.handler)
	}
}

func TestAuthServer_Run_ListenError_ExitsProcess(t *testing.T) {
	cfg := newTestConfig() // вызовет ошибку ListenAndServe
	handler := http.NewServeMux()
	srv, err := NewAuthServer(cfg, environments, handler)
	if err != nil {
		t.Errorf("NewAuthServer() error = %v", err)
	}

	ctx, cancel := context.WithTimeout(context.Background(), time.Millisecond*100)
	go func() {
		time.Sleep(time.Millisecond * 111)
		cancel()
	}()
	err = srv.Run(ctx)
	if err != nil && err.Error() != context.DeadlineExceeded.Error() {
		t.Errorf("expected %v, got %v", context.DeadlineExceeded.Error(), err)
	}
}

// Тестируем интерфейс Server
func TestAuthServer_Interface(t *testing.T) {
	var s Server = &AuthServer{}
	_ = s
}

var _ config.Config = (*testValuesConfig)(nil)

type testValuesConfig struct {
	values config.Values
}

func (t testValuesConfig) Values() config.Values {
	return t.values
}

func (t testValuesConfig) JWThs256SignKey(_ string) {}

func newTestConfig() *testValuesConfig {
	return &testValuesConfig{values: config.Values{Address: "127.0.0.1:0", Debug: true}}
}
