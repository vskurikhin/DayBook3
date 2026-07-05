package tool

import (
	"encoding/base64"
	"errors"
	"testing"

	"github.com/google/uuid"
)

func TestCanClaimUUID(t *testing.T) {
	validUUID := uuid.New()
	encoded := base64.StdEncoding.EncodeToString(validUUID[:])

	tests := []struct {
		name    string
		input   string
		inErr   error
		wantErr bool
		check   func(t *testing.T, got uuid.UUID)
	}{
		{
			name:    "success valid base64 uuid",
			input:   encoded,
			inErr:   nil,
			wantErr: false,
			check: func(t *testing.T, got uuid.UUID) {
				if got != validUUID {
					t.Fatalf("expected %v, got %v", validUUID, got)
				}
			},
		},
		{
			name:    "propagate input error",
			input:   encoded,
			inErr:   errors.New("input error"),
			wantErr: true,
		},
		{
			name:    "invalid base64",
			input:   "!!!invalid!!!",
			inErr:   nil,
			wantErr: true,
		},
		{
			name:    "short decoded bytes",
			input:   base64.StdEncoding.EncodeToString([]byte{1, 2, 3}),
			inErr:   nil,
			wantErr: false,
			check: func(t *testing.T, got uuid.UUID) {
				// проверяем что копирование произошло (частично заполненный UUID)
				if got == uuid.Nil {
					t.Fatalf("expected non-nil uuid")
				}
			},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := CanClaimUUID(tt.input, tt.inErr)

			if tt.wantErr && err == nil {
				t.Fatalf("expected error, got nil")
			}
			if !tt.wantErr && err != nil {
				t.Fatalf("unexpected error: %v", err)
			}

			if err == nil && tt.check != nil {
				tt.check(t, got)
			}
		})
	}
}

func TestCanUUIDParse(t *testing.T) {
	validUUID := uuid.New().String()

	tests := []struct {
		name    string
		input   string
		inErr   error
		wantErr bool
		check   func(t *testing.T, got uuid.UUID)
	}{
		{
			name:    "success valid uuid",
			input:   validUUID,
			inErr:   nil,
			wantErr: false,
			check: func(t *testing.T, got uuid.UUID) {
				if got.String() != validUUID {
					t.Fatalf("expected %v, got %v", validUUID, got)
				}
			},
		},
		{
			name:    "propagate input error",
			input:   validUUID,
			inErr:   errors.New("input error"),
			wantErr: true,
		},
		{
			name:    "invalid uuid string",
			input:   "not-a-uuid",
			inErr:   nil,
			wantErr: true,
		},
		{
			name:    "empty string",
			input:   "",
			inErr:   nil,
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := CanUUIDParse(tt.input, tt.inErr)

			if tt.wantErr && err == nil {
				t.Fatalf("expected error, got nil")
			}
			if !tt.wantErr && err != nil {
				t.Fatalf("unexpected error: %v", err)
			}

			if err == nil && tt.check != nil {
				tt.check(t, got)
			}
		})
	}
}
