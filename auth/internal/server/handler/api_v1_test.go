package handler

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

// mockResourceV1 реализует resources.ResourceV1
type mockResourceV1 struct {
	called bool
}

func (m *mockResourceV1) Ok(w http.ResponseWriter, _ *http.Request) {
	m.called = true
	w.WriteHeader(http.StatusOK)
	_, _ = w.Write([]byte("ok"))
}

func TestNewApiV1_OKRoute(t *testing.T) {
	mock := &mockResourceV1{}

	router := NewApiV1(mock)

	req := httptest.NewRequest(http.MethodGet, OkURL, nil)
	rec := httptest.NewRecorder()

	router.ServeHTTP(rec, req)

	if rec.Code != http.StatusOK {
		t.Fatalf("expected status 200, got %d", rec.Code)
	}

	if rec.Body.String() != "ok" {
		t.Fatalf("expected body 'ok', got %s", rec.Body.String())
	}

	if !mock.called {
		t.Fatal("expected Ok handler to be called")
	}
}
