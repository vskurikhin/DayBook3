package role

import (
	"context"
	"errors"
	"testing"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgtype"
)

type mockRow struct {
	scanFunc func(dest ...any) error
}

func (m mockRow) Scan(dest ...any) error {
	return m.scanFunc(dest...)
}

type mockRows struct {
	index int
	data  []Role
	err   error
}

func (m *mockRows) Next() bool {
	return m.index < len(m.data)
}

func (m *mockRows) Scan(dest ...any) error {
	r := m.data[m.index]
	m.index++

	*(dest[0].(*string)) = r.Role
	*(dest[1].(*pgtype.UUID)) = r.ID
	*(dest[2].(*pgtype.Text)) = r.Description
	*(dest[3].(*string)) = r.UserName
	*(dest[4].(*pgtype.Timestamp)) = r.CreateTime
	*(dest[5].(*pgtype.Timestamp)) = r.UpdateTime
	*(dest[6].(*bool)) = r.Enabled
	*(dest[7].(*bool)) = r.LocalChange
	*(dest[8].(*bool)) = r.Visible
	*(dest[9].(*int32)) = r.Flags

	return nil
}

func (m *mockRows) Close()                                       {}
func (m *mockRows) Err() error                                   { return m.err }
func (m *mockRows) CommandTag() pgconn.CommandTag                { return pgconn.CommandTag{} }
func (m *mockRows) FieldDescriptions() []pgconn.FieldDescription { return nil }
func (m *mockRows) Values() ([]any, error)                       { return nil, nil }
func (m *mockRows) RawValues() [][]byte                          { return nil }
func (m *mockRows) Conn() *pgx.Conn                              { return nil }

type mockDB struct {
	execFunc  func(context.Context, string, ...interface{}) (pgconn.CommandTag, error)
	queryFunc func(context.Context, string, ...interface{}) (pgx.Rows, error)
	rowFunc   func(context.Context, string, ...interface{}) pgx.Row
}

func (m mockDB) Exec(ctx context.Context, sql string, args ...interface{}) (pgconn.CommandTag, error) {
	return m.execFunc(ctx, sql, args...)
}

func (m mockDB) Query(ctx context.Context, sql string, args ...interface{}) (pgx.Rows, error) {
	return m.queryFunc(ctx, sql, args...)
}

func (m mockDB) QueryRow(ctx context.Context, sql string, args ...interface{}) pgx.Row {
	return m.rowFunc(ctx, sql, args...)
}

func TestCreateRole(t *testing.T) {
	expected := Role{Role: "admin"}

	db := mockDB{
		rowFunc: func(ctx context.Context, sql string, args ...interface{}) pgx.Row {
			return mockRow{
				scanFunc: func(dest ...any) error {
					*(dest[0].(*string)) = expected.Role
					return nil
				},
			}
		},
	}

	q := New(db)

	res, err := q.CreateRole(context.Background(), CreateRoleParams{
		Role: "admin",
	})

	if err != nil {
		t.Fatal(err)
	}

	if res.Role != "admin" {
		t.Fatal("unexpected role")
	}
}

func TestDeleteRoleByID(t *testing.T) {
	called := false

	db := mockDB{
		execFunc: func(ctx context.Context, sql string, args ...interface{}) (pgconn.CommandTag, error) {
			called = true
			return pgconn.CommandTag{}, nil
		},
	}

	q := New(db)

	err := q.DeleteRoleByID(context.Background(), pgtype.UUID{})

	if err != nil {
		t.Fatal(err)
	}

	if !called {
		t.Fatal("exec not called")
	}
}

func TestDeleteRoleByName_Error(t *testing.T) {
	db := mockDB{
		execFunc: func(ctx context.Context, sql string, args ...interface{}) (pgconn.CommandTag, error) {
			return pgconn.CommandTag{}, errors.New("db error")
		},
	}

	q := New(db)

	err := q.DeleteRoleByName(context.Background(), "admin")

	if err == nil {
		t.Fatal("expected error")
	}
}

func TestGetRole(t *testing.T) {
	db := mockDB{
		rowFunc: func(ctx context.Context, sql string, args ...interface{}) pgx.Row {
			return mockRow{
				scanFunc: func(dest ...any) error {
					*(dest[0].(*string)) = "admin"
					return nil
				},
			}
		},
	}

	q := New(db)

	r, err := q.GetRole(context.Background(), "admin")

	if err != nil {
		t.Fatal(err)
	}

	if r.Role != "admin" {
		t.Fatal("unexpected role")
	}
}

func TestListRoles(t *testing.T) {
	rows := &mockRows{
		data: []Role{
			{Role: "admin"},
			{Role: "user"},
		},
	}

	db := mockDB{
		queryFunc: func(ctx context.Context, sql string, args ...interface{}) (pgx.Rows, error) {
			return rows, nil
		},
	}

	q := New(db)

	list, err := q.ListRoles(context.Background())

	if err != nil {
		t.Fatal(err)
	}

	if len(list) != 2 {
		t.Fatalf("expected 2 roles got %d", len(list))
	}
}

func TestUpdateRole(t *testing.T) {
	called := false

	db := mockDB{
		execFunc: func(ctx context.Context, sql string, args ...interface{}) (pgconn.CommandTag, error) {
			called = true
			return pgconn.CommandTag{}, nil
		},
	}

	q := New(db)

	err := q.UpdateRole(context.Background(), UpdateRoleParams{
		Role: "admin",
	})

	if err != nil {
		t.Fatal(err)
	}

	if !called {
		t.Fatal("exec not called")
	}
}
