package xerror

import (
	"errors"
	"fmt"
	"testing"

	"github.com/stretchr/testify/require"
)

// mockPgError реализует PgError
type mockPgError struct {
	msg  string
	code string
}

func (m mockPgError) Error() string    { return m.msg }
func (m mockPgError) SQLState() string { return m.code }

func TestIsPgError(t *testing.T) {
	tests := []struct {
		name string
		err  error
		want bool
	}{
		{
			name: "pg error",
			err:  mockPgError{msg: "pg", code: "23505"},
			want: true,
		},
		{
			name: "wrapped pg error",
			err:  fmt.Errorf("%w", mockPgError{msg: "pg", code: "23505"}),
			want: true,
		},
		{
			name: "not pg error",
			err:  errors.New(" обычная ошибка"),
			want: false,
		},
		{
			name: "nil error",
			err:  nil,
			want: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := IsPgError(tt.err)
			require.Equal(t, tt.want, got)
		})
	}
}

func TestClassingPgError(t *testing.T) {
	tests := []struct {
		name string
		err  error
		want error
	}{
		{
			name: "not pg error",
			err:  errors.New("not pg"),
			want: ErrIsNotPgError,
		},
		{
			name: "connection exception",
			err:  mockPgError{code: "08000"},
			want: ErrConnectionException,
		},
		{
			name: "triggered action",
			err:  mockPgError{code: "09000"},
			want: ErrTriggeredActionException,
		},
		{
			name: "invalid transaction",
			err:  mockPgError{code: "0B000"},
			want: ErrInvalidTransactionInitiation,
		},
		{
			name: "unique violation",
			err:  mockPgError{code: "23505"},
			want: ErrUniqueViolation,
		},
		{
			name: "transaction rollback",
			err:  mockPgError{code: "40000"},
			want: ErrTransactionRollback,
		},
		{
			name: "serialization failure",
			err:  mockPgError{code: "40001"},
			want: ErrSerializationFailure,
		},
		{
			name: "deadlock",
			err:  mockPgError{code: "40P01"},
			want: ErrDeadlockDetected,
		},
		{
			name: "syntax error",
			err:  mockPgError{code: "42601"},
			want: ErrSyntaxError,
		},
		{
			name: "forbidden",
			err:  mockPgError{code: "42501"},
			want: ErrForbidden,
		},
		{
			name: "invalid name",
			err:  mockPgError{code: "42602"},
			want: ErrInvalidName,
		},
		{
			name: "undefined column",
			err:  mockPgError{code: "42703"},
			want: ErrUndefinedColumn,
		},
		{
			name: "undefined function",
			err:  mockPgError{code: "42883"},
			want: ErrUndefinedFunction,
		},
		{
			name: "undefined table",
			err:  mockPgError{code: "42P01"},
			want: ErrUndefinedTable,
		},
		{
			name: "undefined parameter",
			err:  mockPgError{code: "42P02"},
			want: ErrUndefinedParameter,
		},
		{
			name: "insufficient resources",
			err:  mockPgError{code: "53000"},
			want: ErrInsufficientResources,
		},
		{
			name: "limit exceeded",
			err:  mockPgError{code: "54000"},
			want: ErrLimitExceeded,
		},
		{
			name: "system error",
			err:  mockPgError{code: "58000"},
			want: ErrSystemError,
		},
		{
			name: "unclassified",
			err:  mockPgError{code: "99999"},
			want: ErrUnclassifiedPgError,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := ClassingPgError(tt.err)
			require.Equal(t, tt.want, got)
		})
	}
}
