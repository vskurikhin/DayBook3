package advisory_lock

import (
	"context"
	"errors"
	"testing"

	"github.com/jackc/pgx/v5/pgconn"
	"github.com/stretchr/testify/require"
	"go.uber.org/mock/gomock"
)

func TestAcquireLock(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	ctx := context.Background()
	key := int64(42)

	tests := []struct {
		name        string
		mockSetup   func(conn *MockPgxConn, row *MockRow)
		expectedOk  bool
		expectError bool
	}{
		{
			name: "lock acquired",
			mockSetup: func(conn *MockPgxConn, row *MockRow) {
				conn.EXPECT().
					QueryRow(ctx, "SELECT pg_try_advisory_lock($1)", key).
					Return(row)

				row.EXPECT().
					Scan(gomock.Any()).
					DoAndReturn(func(dest ...any) error {
						*(dest[0].(*bool)) = true
						return nil
					})
			},
			expectedOk:  true,
			expectError: false,
		},
		{
			name: "lock not acquired",
			mockSetup: func(conn *MockPgxConn, row *MockRow) {
				conn.EXPECT().
					QueryRow(ctx, "SELECT pg_try_advisory_lock($1)", key).
					Return(row)

				row.EXPECT().
					Scan(gomock.Any()).
					DoAndReturn(func(dest ...any) error {
						*(dest[0].(*bool)) = false
						return nil
					})
			},
			expectedOk:  false,
			expectError: false,
		},
		{
			name: "scan error",
			mockSetup: func(conn *MockPgxConn, row *MockRow) {
				conn.EXPECT().
					QueryRow(ctx, "SELECT pg_try_advisory_lock($1)", key).
					Return(row)

				row.EXPECT().
					Scan(gomock.Any()).
					Return(errors.New("scan error"))
			},
			expectedOk:  false,
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			conn := NewMockPgxConn(ctrl)
			row := NewMockRow(ctrl)

			tt.mockSetup(conn, row)

			ok, err := AcquireLock(ctx, conn, key)

			if tt.expectError {
				require.Error(t, err)
			} else {
				require.NoError(t, err)
			}
			require.Equal(t, tt.expectedOk, ok)
		})
	}
}

func TestReleaseLock(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	ctx := context.Background()
	key := int64(42)

	tests := []struct {
		name        string
		mockSetup   func(conn *MockPgxConn)
		expectError bool
	}{
		{
			name: "success",
			mockSetup: func(conn *MockPgxConn) {
				conn.EXPECT().
					Exec(ctx, "SELECT pg_advisory_unlock($1)", key).
					Return(pgconn.CommandTag{}, nil)
			},
			expectError: false,
		},
		{
			name: "exec error",
			mockSetup: func(conn *MockPgxConn) {
				conn.EXPECT().
					Exec(ctx, "SELECT pg_advisory_unlock($1)", key).
					Return(pgconn.CommandTag{}, errors.New("db error"))
			},
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			conn := NewMockPgxConn(ctrl)

			tt.mockSetup(conn)

			err := ReleaseLock(ctx, conn, key)

			if tt.expectError {
				require.Error(t, err)
			} else {
				require.NoError(t, err)
			}
		})
	}
}
