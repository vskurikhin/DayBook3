package db

import (
	"context"
	"errors"
	"testing"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

func TestPool(t *testing.T) {
	type testType struct {
		name        string
		ts          tool.TestStruct
		newPoolFunc func(t gomock.TestReporter, opts ...gomock.ControllerOption) (*PgxPool, *gomock.Controller)
		testFunc    func(t *testing.T, pool *PgxPool) (any, error)
		want        any
	}
	tests := []testType{
		{
			name: "positive #1 pool.Begin",
			ts: tool.TestStruct{
				Enable:  true,
				WantErr: false,
			},
			newPoolFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (*PgxPool, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				poolMock := NewMockPool(ctrl)
				poolMock.EXPECT().Begin(gomock.Any()).Return(&pgxpool.Tx{}, nil).Times(1)
				return &PgxPool{
					pgxPool: poolMock,
				}, ctrl
			},
			testFunc: func(t *testing.T, pool *PgxPool) (any, error) {
				return pool.Begin(context.Background())
			},
			want: &pgxpool.Tx{},
		},
		{
			name: "positive #2 pool.Exec",
			ts: tool.TestStruct{
				Enable:  true,
				WantErr: false,
			},
			newPoolFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (*PgxPool, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				poolMock := NewMockPool(ctrl)
				poolMock.EXPECT().Exec(gomock.Any(), gomock.Any()).Return(pgconn.CommandTag{}, nil).Times(1)
				return &PgxPool{
					pgxPool: poolMock,
				}, ctrl
			},
			testFunc: func(t *testing.T, pool *PgxPool) (any, error) {
				return pool.Exec(context.Background(), "")
			},
			want: pgconn.CommandTag{},
		},
		{
			name: "positive #3 pool.Query",
			ts: tool.TestStruct{
				Enable:  true,
				WantErr: false,
			},
			newPoolFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (*PgxPool, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				poolMock := NewMockPool(ctrl)
				poolMock.EXPECT().Query(gomock.Any(), gomock.Any()).Return(&mockRows{}, nil).Times(1)
				return &PgxPool{
					pgxPool: poolMock,
				}, ctrl
			},
			testFunc: func(t *testing.T, pool *PgxPool) (any, error) {
				return pool.Query(context.Background(), "")
			},
			want: &mockRows{},
		},
		{
			name: "positive #4 pool.QueryRow",
			ts: tool.TestStruct{
				Enable:  true,
				WantErr: false,
			},
			newPoolFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (*PgxPool, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				poolMock := NewMockPool(ctrl)
				poolMock.EXPECT().QueryRow(gomock.Any(), gomock.Any()).Return(&mockRow{}).Times(1)
				return &PgxPool{
					pgxPool: poolMock,
				}, ctrl
			},
			testFunc: func(t *testing.T, pool *PgxPool) (any, error) {
				return pool.QueryRow(context.Background(), ""), nil
			},
			want: &mockRow{},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if tt.ts.Enable {
				pool, ctrl := tt.newPoolFunc(t)
				defer ctrl.Finish()
				got, err := tt.testFunc(t, pool)
				if tt.ts.WantErr && !errors.Is(err, tt.ts.Err) {
					t.Errorf("got error %v, want %v", err, tt.ts.Err)
				} else if !tt.ts.WantErr && err != nil {
					t.Fatal("unexpected error", err)
				}
				assert.Equal(t, tt.want, got)
			}
		})
	}
}

func TestQueryRow_AcquireError(t *testing.T) {
	pool := &PgxPool{
		pgxPool: &mockPool{
			err: errors.New("acquire error"),
		},
	}

	_ = pool.QueryRow(context.Background(), "SELECT 1")
}

func TestPgxPool_Acquire(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	ctx := context.Background()

	tests := []struct {
		name      string
		setupMock func(m *MockPool)
		wantErr   bool
	}{
		{
			name: "success",
			setupMock: func(m *MockPool) {
				m.EXPECT().
					Acquire(ctx).
					Return(&pgxpool.Conn{}, nil)
			},
			wantErr: false,
		},
		{
			name: "error from pool",
			setupMock: func(m *MockPool) {
				m.EXPECT().
					Acquire(ctx).
					Return(nil, errors.New("acquire error"))
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mockPool := NewMockPool(ctrl)

			if tt.name != "nil pool panic protection (optional case)" {
				tt.setupMock(mockPool)
			}

			p := &PgxPool{
				pgxPool: func() Pool {
					return mockPool
				}(),
			}

			conn, err := p.Acquire(ctx)

			if tt.wantErr {
				require.Error(t, err)
				return
			}

			require.NoError(t, err)
			require.NotNil(t, conn)
		})
	}
}

type mockPool struct {
	conn *pgxpool.Conn
	err  error
}

func (m *mockPool) Acquire(_ context.Context) (*pgxpool.Conn, error) {
	if m.err != nil {
		return nil, m.err
	}
	return m.conn, nil
}

func (m *mockPool) Begin(_ context.Context) (pgx.Tx, error) {
	return &pgxpool.Tx{}, nil
}

func (m *mockPool) Exec(_ context.Context, _ string, _ ...any) (pgconn.CommandTag, error) {
	return pgconn.CommandTag{}, nil
}

func (m *mockPool) Close() {
	return
}

func (m *mockPool) Ping(_ context.Context) error {
	return nil
}

func (m *mockPool) Query(_ context.Context, _ string, _ ...any) (pgx.Rows, error) {
	return &mockRows{}, nil
}

func (m *mockPool) QueryRow(_ context.Context, _ string, _ ...any) pgx.Row {
	return &mockRow{}
}

func (m *mockPool) Stat() *pgxpool.Stat {
	return &pgxpool.Stat{}
}

type mockRow struct {
	scanFunc func(dest ...any) error
}

func (m mockRow) Scan(dest ...any) error {
	return m.scanFunc(dest...)
}

type mockRows struct {
	err error
}

func (m *mockRows) Next() bool {
	return false
}

func (m *mockRows) Scan(_ ...any) error {
	return nil
}

func (m *mockRows) Close()                                       {}
func (m *mockRows) Err() error                                   { return m.err }
func (m *mockRows) CommandTag() pgconn.CommandTag                { return pgconn.CommandTag{} }
func (m *mockRows) FieldDescriptions() []pgconn.FieldDescription { return nil }
func (m *mockRows) Values() ([]any, error)                       { return nil, nil }
func (m *mockRows) RawValues() [][]byte                          { return nil }
func (m *mockRows) Conn() *pgx.Conn                              { return nil }
