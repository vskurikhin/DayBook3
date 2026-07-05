package handler

import (
	"encoding/json"
	"errors"
	"log/slog"
	"net/http"

	"github.com/golang-jwt/jwt/v5"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
)

// APIHandler wraps handlers to provide consistent error handling
// This pattern reduces boilerplate in route handlers
type APIHandler func(w http.ResponseWriter, r *http.Request) error

// ServeHTTP implements http.Handler for APIHandler
// This allows APIHandler to be used as a standard handler
func (fn APIHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	done := make(chan struct{})
	go func() {
		defer close(done)
		w.Header().Set("Content-Type", "application/json")

		// Call the actual handler
		//goland:noinspection ALL
		if err := fn(w, r); err != nil {
			// Log the error for debugging
			slog.ErrorContext(ctx, "API", slog.String("error", err.Error()))
			switch {
			case isStatusNoContent(err):
				w.WriteHeader(http.StatusNoContent)
				return
			case isStatusUnauthorized(err):
				w.WriteHeader(http.StatusUnauthorized)
			case isStatusForbidden(err):
				w.WriteHeader(http.StatusForbidden)
			case isStatusConflict(err):
				w.WriteHeader(http.StatusConflict)
			case isStatusServiceUnavailable(err):
				w.WriteHeader(http.StatusServiceUnavailable)
			default:
				w.WriteHeader(http.StatusInternalServerError)
			}

			// Send error response
			encodeErr := json.NewEncoder(w).Encode(resources.APIResponse{
				Success: false,
				Error:   err.Error(),
			})
			if encodeErr != nil {
				slog.ErrorContext(ctx, "API", slog.String("error", encodeErr.Error()))
			}
		}
	}()
	select {
	case <-ctx.Done():
		return
	case <-done:
		return
	}
}

// APISyncHandler wraps handlers to provide consistent error handling
// This pattern reduces boilerplate in route handlers
type APISyncHandler func(w http.ResponseWriter, r *http.Request) error

// ServeHTTP implements http.Handler for APISyncHandler
// This allows APISyncHandler to be used as a standard handler
func (fn APISyncHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	select {
	case <-ctx.Done():
		return
	default:
		w.Header().Set("Content-Type", "application/json")

		// Call the actual handler
		//goland:noinspection ALL
		if err := fn(w, r); err != nil {
			// Log the error for debugging
			slog.ErrorContext(ctx, "API", slog.String("error", err.Error()))
			switch {
			case isStatusNoContent(err):
				w.WriteHeader(http.StatusNoContent)
				return
			case isStatusUnauthorized(err):
				w.WriteHeader(http.StatusUnauthorized)
			case isStatusForbidden(err):
				w.WriteHeader(http.StatusForbidden)
			case isStatusNotFound(err):
				w.WriteHeader(http.StatusNotFound)
			case isStatusConflict(err):
				w.WriteHeader(http.StatusConflict)
			case isStatusServiceUnavailable(err):
				w.WriteHeader(http.StatusServiceUnavailable)
			default:
				w.WriteHeader(http.StatusInternalServerError)
			}

			// Send error response
			encodeErr := json.NewEncoder(w).Encode(resources.APIResponse{
				Success: false,
				Error:   err.Error(),
			})
			if encodeErr != nil {
				slog.ErrorContext(ctx, "API", slog.String("error", encodeErr.Error()))
			}
		}
	}
}

func isStatusNoContent(err error) bool {
	return errors.Is(err, jwt.ErrTokenExpired) ||
		errors.Is(err, xerror.ErrInvalidToken) ||
		errors.Is(err, xerror.ErrJInvalidUserName) ||
		errors.Is(err, xerror.ErrLogout) ||
		errors.Is(err, xerror.ErrSessionTimeExpired)
}

func isStatusUnauthorized(err error) bool {
	return errors.Is(err, xerror.ErrNoCookie) ||
		errors.Is(err, xerror.ErrInvalidPassword)
}

func isStatusForbidden(err error) bool {
	return errors.Is(err, xerror.ErrForbidden)
}

func isStatusNotFound(err error) bool {
	return errors.Is(err, xerror.ErrNoRows)
}

func isStatusConflict(err error) bool {
	return errors.Is(err, xerror.ErrUserExists)
}

func isStatusServiceUnavailable(err error) bool {
	return errors.Is(err, xerror.ErrUndefinedColumn) ||
		errors.Is(err, xerror.ErrUndefinedFunction) ||
		errors.Is(err, xerror.ErrUndefinedTable)
}
