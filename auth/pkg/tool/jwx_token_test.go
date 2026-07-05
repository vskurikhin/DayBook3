package tool

import (
	"testing"

	"github.com/lestrrat-go/jwx/v3/jwt"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
)

func TestJwxTokenSubject(t *testing.T) {
	tests := []struct {
		name      string
		setup     func() jwt.Token
		want      string
		wantError bool
	}{
		{
			name: "success",
			setup: func() jwt.Token {
				tok := jwt.New()
				_ = tok.Set(jwt.SubjectKey, "user-123")
				return tok
			},
			want:      "user-123",
			wantError: false,
		},
		{
			name: "missing subject",
			setup: func() jwt.Token {
				return jwt.New()
			},
			want:      "",
			wantError: true,
		},
		{
			name: "empty subject still valid",
			setup: func() jwt.Token {
				tok := jwt.New()
				_ = tok.Set(jwt.SubjectKey, "")
				return tok
			},
			want:      "",
			wantError: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			token := tt.setup()

			got, err := JwxTokenSubject(token)

			if tt.wantError {
				if err == nil {
					t.Fatalf("expected error, got nil")
				}
				if err.Error() != xerror.InvalidToken {
					t.Fatalf("expected error %q, got %v", xerror.InvalidToken, err)
				}
				return
			}

			if err != nil {
				t.Fatalf("unexpected error: %v", err)
			}

			if got != tt.want {
				t.Fatalf("expected %q, got %q", tt.want, got)
			}
		})
	}
}

func TestJwxTokenJTI(t *testing.T) {
	tests := []struct {
		name      string
		setup     func() jwt.Token
		want      string
		wantError bool
	}{
		{
			name: "success",
			setup: func() jwt.Token {
				tok := jwt.New()
				_ = tok.Set(jwt.JwtIDKey, "jti-123")
				return tok
			},
			want:      "jti-123",
			wantError: false,
		},
		{
			name: "missing jti",
			setup: func() jwt.Token {
				return jwt.New()
			},
			want:      "",
			wantError: true,
		},
		{
			name: "empty jti still valid",
			setup: func() jwt.Token {
				tok := jwt.New()
				_ = tok.Set(jwt.JwtIDKey, "")
				return tok
			},
			want:      "",
			wantError: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			token := tt.setup()

			got, err := JwxTokenJTI(token)

			if tt.wantError {
				if err == nil {
					t.Fatalf("expected error, got nil")
				}
				if err.Error() != xerror.InvalidToken {
					t.Fatalf("expected error %q, got %v", xerror.InvalidToken, err)
				}
				return
			}

			if err != nil {
				t.Fatalf("unexpected error: %v", err)
			}

			if got != tt.want {
				t.Fatalf("expected %q, got %q", tt.want, got)
			}
		})
	}
}

func TestJwxTokenIssuer(t *testing.T) {
	tests := []struct {
		name      string
		setup     func() jwt.Token
		want      string
		wantError bool
	}{
		{
			name: "success",
			setup: func() jwt.Token {
				tok := jwt.New()
				_ = tok.Set(jwt.IssuerKey, "issuer-xyz")
				return tok
			},
			want:      "issuer-xyz",
			wantError: false,
		},
		{
			name: "missing issuer",
			setup: func() jwt.Token {
				return jwt.New()
			},
			want:      "",
			wantError: true,
		},
		{
			name: "empty issuer still valid",
			setup: func() jwt.Token {
				tok := jwt.New()
				_ = tok.Set(jwt.IssuerKey, "")
				return tok
			},
			want:      "",
			wantError: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			token := tt.setup()

			got, err := JwxTokenIssuer(token)

			if tt.wantError {
				if err == nil {
					t.Fatalf("expected error, got nil")
				}
				if err.Error() != xerror.InvalidToken {
					t.Fatalf("expected error %q, got %v", xerror.InvalidToken, err)
				}
				return
			}

			if err != nil {
				t.Fatalf("unexpected error: %v", err)
			}

			if got != tt.want {
				t.Fatalf("expected %q, got %q", tt.want, got)
			}
		})
	}
}
