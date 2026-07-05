package handler

import (
	"context"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/golang-jwt/jwt/v5"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
)

func TestAPIHandler_Success(t *testing.T) {
	handler := APIHandler(func(w http.ResponseWriter, r *http.Request) error {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"success":true}`))
		return nil
	})

	req := httptest.NewRequest(http.MethodGet, "/", nil)
	rec := httptest.NewRecorder()

	handler.ServeHTTP(rec, req)

	if rec.Code != http.StatusOK {
		t.Fatalf("expected 200, got %d", rec.Code)
	}
}

func TestAPIHandler_ErrorStatuses(t *testing.T) {
	tests := []struct {
		name       string
		err        error
		wantStatus int
	}{
		{
			name:       "no content - jwt expired",
			err:        jwt.ErrTokenExpired,
			wantStatus: http.StatusNoContent,
		},
		{
			name:       "unauthorized",
			err:        xerror.ErrInvalidPassword,
			wantStatus: http.StatusUnauthorized,
		},
		{
			name:       "forbidden",
			err:        xerror.ErrForbidden,
			wantStatus: http.StatusForbidden,
		},
		{
			name:       "conflict",
			err:        xerror.ErrUserExists,
			wantStatus: http.StatusConflict,
		},
		{
			name:       "service unavailable",
			err:        xerror.ErrUndefinedTable,
			wantStatus: http.StatusServiceUnavailable,
		},
		{
			name:       "default internal error",
			err:        context.Canceled,
			wantStatus: http.StatusInternalServerError,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			handler := APIHandler(func(w http.ResponseWriter, r *http.Request) error {
				return tt.err
			})

			req := httptest.NewRequest(http.MethodGet, "/", nil)
			rec := httptest.NewRecorder()

			handler.ServeHTTP(rec, req)

			if rec.Code != tt.wantStatus {
				t.Fatalf("expected status %d, got %d", tt.wantStatus, rec.Code)
			}

			// NoContent не пишет body
			if tt.wantStatus == http.StatusNoContent {
				return
			}

			var resp resources.APIResponse
			if err := json.Unmarshal(rec.Body.Bytes(), &resp); err != nil {
				t.Fatalf("invalid json response: %v", err)
			}

			if resp.Success != false {
				t.Fatalf("expected success=false")
			}
			if resp.Error == "" {
				t.Fatalf("expected error message")
			}
		})
	}
}

func TestAPIHandler_ContextCanceled(t *testing.T) {
	handler := APIHandler(func(w http.ResponseWriter, r *http.Request) error {
		return nil
	})

	req := httptest.NewRequest(http.MethodGet, "/", nil)
	ctx, cancel := context.WithCancel(req.Context())
	cancel()
	req = req.WithContext(ctx)

	rec := httptest.NewRecorder()

	handler.ServeHTTP(rec, req)

	// ничего не должно произойти
	if rec.Code != 200 && rec.Code != 0 {
		t.Fatalf("unexpected status: %d", rec.Code)
	}
}

func TestAPISyncHandler_ErrorStatuses(t *testing.T) {
	tests := []struct {
		name       string
		err        error
		wantStatus int
	}{
		{
			name:       "no content",
			err:        xerror.ErrLogout,
			wantStatus: http.StatusNoContent,
		},
		{
			name:       "unauthorized",
			err:        xerror.ErrInvalidPassword,
			wantStatus: http.StatusUnauthorized,
		},
		{
			name:       "forbidden",
			err:        xerror.ErrForbidden,
			wantStatus: http.StatusForbidden,
		},
		{
			name:       "conflict",
			err:        xerror.ErrUserExists,
			wantStatus: http.StatusConflict,
		},
		{
			name:       "service unavailable",
			err:        xerror.ErrUndefinedFunction,
			wantStatus: http.StatusServiceUnavailable,
		},
		{
			name:       "default",
			err:        context.DeadlineExceeded,
			wantStatus: http.StatusInternalServerError,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			handler := APISyncHandler(func(w http.ResponseWriter, r *http.Request) error {
				return tt.err
			})

			req := httptest.NewRequest(http.MethodGet, "/", nil)
			rec := httptest.NewRecorder()

			handler.ServeHTTP(rec, req)

			if rec.Code != tt.wantStatus {
				t.Fatalf("expected %d, got %d", tt.wantStatus, rec.Code)
			}

			if tt.wantStatus == http.StatusNoContent {
				return
			}

			var resp resources.APIResponse
			if err := json.Unmarshal(rec.Body.Bytes(), &resp); err != nil {
				t.Fatalf("invalid json: %v", err)
			}

			if resp.Success != false {
				t.Fatalf("expected success=false")
			}
		})
	}
}

func TestIsStatusFunctions(t *testing.T) {
	if !isStatusNoContent(jwt.ErrTokenExpired) {
		t.Fatal("expected true for isStatusNoContent")
	}

	if !isStatusUnauthorized(xerror.ErrInvalidPassword) {
		t.Fatal("expected true for isStatusUnauthorized")
	}

	if !isStatusForbidden(xerror.ErrForbidden) {
		t.Fatal("expected true for isStatusForbidden")
	}

	if !isStatusConflict(xerror.ErrUserExists) {
		t.Fatal("expected true for isStatusConflict")
	}

	if !isStatusServiceUnavailable(xerror.ErrUndefinedColumn) {
		t.Fatal("expected true for isStatusServiceUnavailable")
	}

	// negative cases
	if isStatusUnauthorized(nil) {
		t.Fatal("expected false")
	}
	if isStatusForbidden(nil) {
		t.Fatal("expected false")
	}
}
