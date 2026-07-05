package tool

import (
	"testing"

	"golang.org/x/crypto/bcrypt"
)

func TestHash(t *testing.T) {
	tests := []struct {
		name      string
		password  string
		cost      int
		wantError bool
	}{
		{
			name:      "valid password default cost",
			password:  "secret123",
			cost:      bcrypt.DefaultCost,
			wantError: false,
		},
		{
			name:      "empty password",
			password:  "",
			cost:      bcrypt.DefaultCost,
			wantError: false,
		},
		{
			name:      "too high cost",
			password:  "secret123",
			cost:      100, // больше MaxCost
			wantError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			hash, err := Hash(tt.password, tt.cost)

			if tt.wantError {
				if err == nil {
					t.Fatalf("expected error, got nil")
				}
				return
			}

			if err != nil {
				t.Fatalf("unexpected error: %v", err)
			}

			if hash == "" {
				t.Fatalf("expected non-empty hash")
			}

			// bcrypt hash всегда отличается от пароля
			if hash == tt.password {
				t.Fatalf("hash should not equal password")
			}
		})
	}
}

func TestVerify(t *testing.T) {
	password := "my-secret-password"

	hash, err := Hash(password, bcrypt.DefaultCost)
	if err != nil {
		t.Fatalf("failed to hash password: %v", err)
	}

	tests := []struct {
		name     string
		hashed   string
		password string
		expected bool
	}{
		{
			name:     "correct password",
			hashed:   hash,
			password: password,
			expected: true,
		},
		{
			name:     "wrong password",
			hashed:   hash,
			password: "wrong-password",
			expected: false,
		},
		{
			name:     "empty password",
			hashed:   hash,
			password: "",
			expected: false,
		},
		{
			name:     "empty hash",
			hashed:   "",
			password: password,
			expected: false,
		},
		{
			name:     "invalid hash format",
			hashed:   "not-a-bcrypt-hash",
			password: password,
			expected: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := Verify(tt.hashed, tt.password)

			if result != tt.expected {
				t.Fatalf("expected %v, got %v", tt.expected, result)
			}
		})
	}
}

func TestHashAndVerify_Integration(t *testing.T) {
	password := "integration-test-password"

	hash1, err := Hash(password, bcrypt.DefaultCost)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	hash2, err := Hash(password, bcrypt.DefaultCost)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	// Хэши должны быть разными (salt)
	if hash1 == hash2 {
		t.Fatalf("expected different hashes for same password")
	}

	// Оба должны валидироваться
	if !Verify(hash1, password) {
		t.Fatalf("hash1 should be valid")
	}
	if !Verify(hash2, password) {
		t.Fatalf("hash2 should be valid")
	}
}
