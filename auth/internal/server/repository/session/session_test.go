package session

import (
	"context"
	"errors"
	"testing"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/stretchr/testify/assert"
	"go.uber.org/mock/gomock"
)

type mockRow struct {
	scanFunc func(dest ...any) error
}

func (m *mockRow) Scan(dest ...any) error {
	return m.scanFunc(dest...)
}

// 🧪 CreateSession
func TestQueries_CreateSession(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	tests := []struct {
		name      string
		mockSetup func(db *MockDBTX)
		wantErr   bool
	}{
		{
			name: "success",
			mockSetup: func(db *MockDBTX) {
				db.EXPECT().
					QueryRow(gomock.Any(), createSession, gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(&mockRow{
						scanFunc: func(dest ...any) error {
							return nil
						},
					})
			},
			wantErr: false,
		},
		{
			name: "scan error",
			mockSetup: func(db *MockDBTX) {
				db.EXPECT().
					QueryRow(gomock.Any(), createSession, gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(&mockRow{
						scanFunc: func(dest ...any) error {
							return errors.New("scan error")
						},
					})
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mockDB := NewMockDBTX(ctrl)
			tt.mockSetup(mockDB)

			q := New(mockDB)

			_, err := q.CreateSession(context.Background(), CreateSessionParams{})

			if tt.wantErr {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
			}
		})
	}
}

// 🧪 GetSession
func TestQueries_GetSession(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	tests := []struct {
		name      string
		mockSetup func(db *MockDBTX)
		wantErr   bool
	}{
		{
			name: "success",
			mockSetup: func(db *MockDBTX) {
				db.EXPECT().
					QueryRow(gomock.Any(), getSession, gomock.Any(), gomock.Any(), gomock.Any()).
					Return(&mockRow{
						scanFunc: func(dest ...any) error {
							return nil
						},
					})
			},
		},
		{
			name: "scan error",
			mockSetup: func(db *MockDBTX) {
				db.EXPECT().
					QueryRow(gomock.Any(), getSession, gomock.Any(), gomock.Any(), gomock.Any()).
					Return(&mockRow{
						scanFunc: func(dest ...any) error {
							return errors.New("error")
						},
					})
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mockDB := NewMockDBTX(ctrl)
			tt.mockSetup(mockDB)

			q := New(mockDB)

			_, err := q.GetSession(context.Background(), GetSessionParams{})

			if tt.wantErr {
				assert.Error(t, err)
			}
		})
	}
}

// 🧪 DeleteUserAttrs
func TestQueries_DeleteUserAttrs(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	tests := []struct {
		name      string
		mockSetup func(db *MockDBTX)
		wantErr   bool
	}{
		{
			name: "success",
			mockSetup: func(db *MockDBTX) {
				db.EXPECT().
					Exec(gomock.Any(), deleteSession, gomock.Any(), gomock.Any(), gomock.Any()).
					Return(pgconn.CommandTag{}, nil)
			},
		},
		{
			name: "exec error",
			mockSetup: func(db *MockDBTX) {
				db.EXPECT().
					Exec(gomock.Any(), deleteSession, gomock.Any(), gomock.Any(), gomock.Any()).
					Return(pgconn.CommandTag{}, errors.New("error"))
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mockDB := NewMockDBTX(ctrl)
			tt.mockSetup(mockDB)

			q := New(mockDB)

			err := q.DeleteSession(context.Background(), DeleteSessionParams{})

			if tt.wantErr {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
			}
		})
	}
}

// 🧪 UpdateSession
func TestQueries_UpdateSession(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	tests := []struct {
		name      string
		mockSetup func(db *MockDBTX)
		wantErr   bool
	}{
		{
			name: "success",
			mockSetup: func(db *MockDBTX) {
				db.EXPECT().
					Exec(gomock.Any(), updateSession,
						gomock.Any(), gomock.Any(), gomock.Any(),
						gomock.Any(), gomock.Any(), gomock.Any(),
					).
					Return(pgconn.CommandTag{}, nil)
			},
		},
		{
			name: "exec error",
			mockSetup: func(db *MockDBTX) {
				db.EXPECT().
					Exec(gomock.Any(), updateSession,
						gomock.Any(), gomock.Any(), gomock.Any(),
						gomock.Any(), gomock.Any(), gomock.Any(),
					).
					Return(pgconn.CommandTag{}, errors.New("error"))
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mockDB := NewMockDBTX(ctrl)
			tt.mockSetup(mockDB)

			q := New(mockDB)

			err := q.UpdateSession(context.Background(), UpdateSessionParams{})

			if tt.wantErr {
				assert.Error(t, err)
			}
		})
	}
}

// 🧪 ListSessions
func TestQueries_ListSessions(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	tests := []struct {
		name      string
		mockSetup func(db *MockDBTX)
		wantErr   bool
	}{
		{
			name: "success",
			mockSetup: func(db *MockDBTX) {
				rows := &mockRows{
					next: []bool{true, false},
				}

				db.EXPECT().
					Query(gomock.Any(), listSessions).
					Return(rows, nil)
			},
		},
		{
			name: "query error",
			mockSetup: func(db *MockDBTX) {
				db.EXPECT().
					Query(gomock.Any(), listSessions).
					Return(nil, errors.New("error"))
			},
			wantErr: true,
		},
		{
			name: "scan error",
			mockSetup: func(db *MockDBTX) {
				rows := &mockRows{
					next:    []bool{true},
					scanErr: errors.New("scan error"),
				}

				db.EXPECT().
					Query(gomock.Any(), listSessions).
					Return(rows, nil)
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mockDB := NewMockDBTX(ctrl)
			tt.mockSetup(mockDB)

			q := New(mockDB)

			_, err := q.ListSessions(context.Background())

			if tt.wantErr {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
			}
		})
	}
}

// mock Rows
type mockRows struct {
	next    []bool
	scanErr error
	idx     int
}

func (m *mockRows) Close() {}

func (m *mockRows) Err() error {
	return nil
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
