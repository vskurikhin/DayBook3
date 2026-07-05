package tool

import (
	"testing"

	"github.com/golang-jwt/jwt/v5"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
)

func TestExtractJTI(t *testing.T) {
	tests := []struct {
		name      string
		claims    jwt.Claims
		wantJTI   string
		wantError bool
	}{
		{
			name: "success with valid CustomClaims",
			claims: &CustomClaims{
				Jti: "test-jti-123",
			},
			wantJTI:   "test-jti-123",
			wantError: false,
		},
		{
			name: "success with empty JTI",
			claims: &CustomClaims{
				Jti: "",
			},
			wantJTI:   "",
			wantError: false,
		},
		{
			name: "invalid claims type (RegisteredClaims)",
			claims: &jwt.RegisteredClaims{
				ID: "some-id",
			},
			wantJTI:   "",
			wantError: true,
		},
		{
			name:      "nil claims",
			claims:    nil,
			wantJTI:   "",
			wantError: true,
		},
		{
			name: "wrong struct type implementing jwt.Claims",
			claims: jwt.MapClaims{
				"jti": "map-jti",
			},
			wantJTI:   "",
			wantError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := ExtractJTI(tt.claims)

			if tt.wantError {
				if err == nil {
					t.Fatalf("expected error, got nil")
				}
				if err != xerror.ErrInvalidTokenClaims {
					t.Fatalf("expected ErrInvalidTokenClaims, got %v", err)
				}
				return
			}

			if err != nil {
				t.Fatalf("unexpected error: %v", err)
			}

			if got != tt.wantJTI {
				t.Fatalf("expected JTI %q, got %q", tt.wantJTI, got)
			}
		})
	}
}
