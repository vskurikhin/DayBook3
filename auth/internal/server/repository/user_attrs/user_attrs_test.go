package user_attrs

import (
	"context"
	"errors"
	"testing"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/stretchr/testify/assert"
	"go.uber.org/mock/gomock"
)

// 🧪 CreateUserAttrs
func TestQueries_CreateUserAttrs(t *testing.T) {
	tests := []struct {
		name    string
		setup   func(*MockDBTX)
		wantErr bool
	}{
		{
			name: "success",
			setup: func(db *MockDBTX) {
				db.EXPECT().
					QueryRow(gomock.Any(), createUserAttrs,
						gomock.Any(), gomock.Any(), gomock.Any(),
					).
					Return(&mockRow{scanFunc: func(...any) error { return nil }})
			},
		},
		{
			name: "scan error",
			setup: func(db *MockDBTX) {
				db.EXPECT().
					QueryRow(gomock.Any(), createUserAttrs,
						gomock.Any(), gomock.Any(), gomock.Any(),
					).
					Return(&mockRow{scanFunc: func(...any) error {
						return errors.New("scan error")
					}})
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockDB := NewMockDBTX(ctrl)
			tt.setup(mockDB)

			q := New(mockDB)

			_, err := q.CreateUserAttrs(context.Background(), CreateUserAttrsParams{})

			if tt.wantErr {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
			}
		})
	}
}

// 🧪 GetUserAttrs
func TestQueries_GetUserAttrs(t *testing.T) {
	tests := []struct {
		name    string
		setup   func(*MockDBTX)
		wantErr bool
	}{
		{
			name: "success",
			setup: func(db *MockDBTX) {
				db.EXPECT().
					QueryRow(gomock.Any(), getUserAttrs, gomock.Any()).
					Return(&mockRow{scanFunc: func(...any) error { return nil }})
			},
		},
		{
			name: "scan error",
			setup: func(db *MockDBTX) {
				db.EXPECT().
					QueryRow(gomock.Any(), getUserAttrs, gomock.Any()).
					Return(&mockRow{scanFunc: func(...any) error {
						return errors.New("error")
					}})
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockDB := NewMockDBTX(ctrl)
			tt.setup(mockDB)

			q := New(mockDB)

			_, err := q.GetUserAttrs(context.Background(), "user")

			if tt.wantErr {
				assert.Error(t, err)
			}
		})
	}
}

// 🧪 DeleteUserAttrs
func TestQueries_DeleteUserAttrs(t *testing.T) {
	tests := []struct {
		name    string
		setup   func(*MockDBTX)
		wantErr bool
	}{
		{
			name: "success",
			setup: func(db *MockDBTX) {
				db.EXPECT().
					Exec(gomock.Any(), deleteUserAttrs, "user").
					Return(pgconn.CommandTag{}, nil)
			},
		},
		{
			name: "exec error",
			setup: func(db *MockDBTX) {
				db.EXPECT().
					Exec(gomock.Any(), deleteUserAttrs, "user").
					Return(pgconn.CommandTag{}, errors.New("error"))
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockDB := NewMockDBTX(ctrl)
			tt.setup(mockDB)

			q := New(mockDB)

			err := q.DeleteUserAttrs(context.Background(), "user")

			if tt.wantErr {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
			}
		})
	}
}

// 🧪 UpdateUserAttrs
func TestQueries_UpdateUserAttrs(t *testing.T) {
	tests := []struct {
		name    string
		setup   func(*MockDBTX)
		wantErr bool
	}{
		{
			name: "success",
			setup: func(db *MockDBTX) {
				db.EXPECT().
					Exec(gomock.Any(), updateUserAttrs,
						gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(),
					).
					Return(pgconn.CommandTag{}, nil)
			},
		},
		{
			name: "exec error",
			setup: func(db *MockDBTX) {
				db.EXPECT().
					Exec(gomock.Any(), updateUserAttrs,
						gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(),
					).
					Return(pgconn.CommandTag{}, errors.New("error"))
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockDB := NewMockDBTX(ctrl)
			tt.setup(mockDB)

			q := New(mockDB)

			err := q.UpdateUserAttrs(context.Background(), UpdateUserAttrsParams{})

			if tt.wantErr {
				assert.Error(t, err)
			}
		})
	}
}

// 🧪 ListUserAttrs
func TestQueries_ListUserAttrs(t *testing.T) {
	tests := []struct {
		name    string
		setup   func(*MockDBTX)
		wantErr bool
	}{
		{
			name: "success",
			setup: func(db *MockDBTX) {
				rows := &mockRows{
					next: []bool{true, false},
				}

				db.EXPECT().
					Query(gomock.Any(), listUserAttrs).
					Return(rows, nil)
			},
		},
		{
			name: "query error",
			setup: func(db *MockDBTX) {
				db.EXPECT().
					Query(gomock.Any(), listUserAttrs).
					Return(nil, errors.New("error"))
			},
			wantErr: true,
		},
		{
			name: "scan error",
			setup: func(db *MockDBTX) {
				rows := &mockRows{
					next:    []bool{true},
					scanErr: errors.New("scan error"),
				}

				db.EXPECT().
					Query(gomock.Any(), listUserAttrs).
					Return(rows, nil)
			},
			wantErr: true,
		},
		{
			name: "rows.Err error",
			setup: func(db *MockDBTX) {
				rows := &mockRows{
					next: []bool{false},
					err:  errors.New("rows error"),
				}

				db.EXPECT().
					Query(gomock.Any(), listUserAttrs).
					Return(rows, nil)
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockDB := NewMockDBTX(ctrl)
			tt.setup(mockDB)

			q := New(mockDB)

			_, err := q.ListUserAttrs(context.Background())

			if tt.wantErr {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
			}
		})
	}
}

// 📦 Общие моки (Row + Rows)
// --- mock Row ---
type mockRow struct {
	scanFunc func(dest ...any) error
}

func (m *mockRow) Scan(dest ...any) error {
	return m.scanFunc(dest...)
}

// ✅ mockRows
type mockRows struct {
	next    []bool
	scanErr error
	idx     int
	err     error
}

func (m *mockRows) Close() {}

func (m *mockRows) Err() error {
	return m.err
}

func (m *mockRows) CommandTag() pgconn.CommandTag {
	return pgconn.CommandTag{}
}

func (m *mockRows) FieldDescriptions() []pgconn.FieldDescription {
	return nil
}

func (m *mockRows) Next() bool {
	if m.idx >= len(m.next) {
		return false
	}
	val := m.next[m.idx]
	m.idx++
	return val
}

func (m *mockRows) Scan(_ ...any) error {
	return m.scanErr
}

func (m *mockRows) Values() ([]any, error) {
	return nil, nil
}

func (m *mockRows) RawValues() [][]byte {
	return nil
}

func (m *mockRows) Conn() *pgx.Conn {
	return nil
}
