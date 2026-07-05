package handler

import (
	"context"
	"errors"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

// Success case: handler returns nil
func TestAPISyncHandler_Success(t *testing.T) {

	handler := APISyncHandler(func(w http.ResponseWriter, r *http.Request) error {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"message":"ok"}`))
		return nil
	})

	req := httptest.NewRequest(http.MethodGet, "/test", nil)
	rec := httptest.NewRecorder()

	handler.ServeHTTP(rec, req)

	if rec.Code != http.StatusOK {
		t.Fatalf("expected status 200, got %d", rec.Code)
	}

	if !strings.Contains(rec.Body.String(), "ok") {
		t.Fatalf("unexpected body: %s", rec.Body.String())
	}

	if rec.Header().Get("Content-Type") != "application/json" {
		t.Fatalf("expected Content-Type application/json")
	}
}

// Error case: handler returns error
func TestAPISyncHandler_ReturnsError(t *testing.T) {

	handler := APISyncHandler(func(w http.ResponseWriter, r *http.Request) error {
		return errors.New("test error")
	})

	req := httptest.NewRequest(http.MethodGet, "/test", nil)
	rec := httptest.NewRecorder()

	handler.ServeHTTP(rec, req)

	if rec.Code != http.StatusInternalServerError {
		t.Fatalf("expected status 500, got %d", rec.Code)
	}

	body := rec.Body.String()

	if !strings.Contains(body, "false") {
		t.Fatalf("expected success=false in response: %s", body)
	}
}

// Context canceled before execution
func TestAPISyncHandler_ContextCanceled(t *testing.T) {

	handler := APISyncHandler(func(w http.ResponseWriter, r *http.Request) error {
		t.Fatal("handler should not be called when context is canceled")
		return nil
	})

	ctx, cancel := context.WithCancel(context.Background())
	cancel()

	req := httptest.NewRequest(http.MethodGet, "/test", nil).WithContext(ctx)
	rec := httptest.NewRecorder()

	handler.ServeHTTP(rec, req)

	// Handler should not write anything
	if rec.Body.Len() != 0 {
		t.Fatalf("expected empty body when context canceled, got %s", rec.Body.String())
	}
}

// Ensure Content-Type header always set
func TestAPISyncHandler_ContentTypeHeader(t *testing.T) {

	handler := APISyncHandler(func(w http.ResponseWriter, r *http.Request) error {
		return nil
	})

	req := httptest.NewRequest(http.MethodGet, "/test", nil)
	rec := httptest.NewRecorder()

	handler.ServeHTTP(rec, req)

	if rec.Header().Get("Content-Type") != "application/json" {
		t.Fatalf("expected Content-Type application/json, got %s",
			rec.Header().Get("Content-Type"))
	}
}
