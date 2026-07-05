package user_name

import (
	"context"
	"errors"
	"testing"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgtype"
)

type mockRow struct {
	scan func(dest ...any) error
}

func (m mockRow) Scan(dest ...any) error {
	return m.scan(dest...)
}

type mockRows struct {
	index int
	data  []UserName
	err   error
}

func (m *mockRows) Next() bool {
	return m.index < len(m.data)
}

func (m *mockRows) Scan(dest ...any) error {
	r := m.data[m.index]
	m.index++

	*(dest[0].(*string)) = r.UserName
	*(dest[1].(*pgtype.UUID)) = r.ID
	*(dest[2].(*string)) = r.Password
	*(dest[3].(*pgtype.Timestamp)) = r.CreateTime
	*(dest[4].(*pgtype.Timestamp)) = r.UpdateTime
	*(dest[5].(*bool)) = r.Enabled
	*(dest[6].(*bool)) = r.LocalChange
	*(dest[7].(*bool)) = r.Visible
	*(dest[8].(*int32)) = r.Flags

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
	exec  func(context.Context, string, ...interface{}) (pgconn.CommandTag, error)
	query func(context.Context, string, ...interface{}) (pgx.Rows, error)
	row   func(context.Context, string, ...interface{}) pgx.Row
}

func (m mockDB) Exec(ctx context.Context, sql string, args ...interface{}) (pgconn.CommandTag, error) {
	return m.exec(ctx, sql, args...)
}

func (m mockDB) Query(ctx context.Context, sql string, args ...interface{}) (pgx.Rows, error) {
	return m.query(ctx, sql, args...)
}

func (m mockDB) QueryRow(ctx context.Context, sql string, args ...interface{}) pgx.Row {
	return m.row(ctx, sql, args...)
}

func TestCreateUserName(t *testing.T) {
	db := mockDB{
		row: func(ctx context.Context, sql string, args ...interface{}) pgx.Row {
			return mockRow{
				scan: func(dest ...any) error {
					*(dest[0].(*string)) = "alice"
					return nil
				},
			}
		},
	}

	q := New(db)

	res, err := q.CreateUserName(context.Background(), CreateUserNameParams{
		UserName: "alice",
		Password: "secret",
	})

	if err != nil {
		t.Fatal(err)
	}

	if res.UserName != "alice" {
		t.Fatalf("expected alice got %s", res.UserName)
	}
}

func TestDeleteUserNameByID(t *testing.T) {
	called := false

	db := mockDB{
		exec: func(ctx context.Context, sql string, args ...interface{}) (pgconn.CommandTag, error) {
			called = true
			return pgconn.CommandTag{}, nil
		},
	}

	q := New(db)

	err := q.DeleteUserNameByID(context.Background(), pgtype.UUID{})

	if err != nil {
		t.Fatal(err)
	}

	if !called {
		t.Fatal("Exec not called")
	}
}

func TestDeleteUserNameByName_Error(t *testing.T) {
	db := mockDB{
		exec: func(ctx context.Context, sql string, args ...interface{}) (pgconn.CommandTag, error) {
			return pgconn.CommandTag{}, errors.New("db error")
		},
	}

	q := New(db)

	err := q.DeleteUserNameByName(context.Background(), "alice")

	if err == nil {
		t.Fatal("expected error")
	}
}

func TestGetUserName(t *testing.T) {
	db := mockDB{
		row: func(ctx context.Context, sql string, args ...interface{}) pgx.Row {
			return mockRow{
				scan: func(dest ...any) error {
					*(dest[0].(*string)) = "bob"
					return nil
				},
			}
		},
	}

	q := New(db)

	res, err := q.GetUserName(context.Background(), "bob")

	if err != nil {
		t.Fatal(err)
	}

	if res.UserName != "bob" {
		t.Fatal("unexpected username")
	}
}

func TestListUserNames(t *testing.T) {
	rows := &mockRows{
		data: []UserName{
			{UserName: "alice"},
			{UserName: "bob"},
		},
	}

	db := mockDB{
		query: func(ctx context.Context, sql string, args ...interface{}) (pgx.Rows, error) {
			return rows, nil
		},
	}

	q := New(db)

	list, err := q.ListUserNames(context.Background())

	if err != nil {
		t.Fatal(err)
	}

	if len(list) != 2 {
		t.Fatalf("expected 2 users got %d", len(list))
	}
}

func TestListUserNames_QueryError(t *testing.T) {
	db := mockDB{
		query: func(ctx context.Context, sql string, args ...interface{}) (pgx.Rows, error) {
			return nil, errors.New("query error")
		},
	}

	q := New(db)

	_, err := q.ListUserNames(context.Background())

	if err == nil {
		t.Fatal("expected error")
	}
}

func TestUpdateUserName(t *testing.T) {
	called := false

	db := mockDB{
		exec: func(ctx context.Context, sql string, args ...interface{}) (pgconn.CommandTag, error) {
			called = true
			return pgconn.CommandTag{}, nil
		},
	}

	q := New(db)

	err := q.UpdateUserName(context.Background(), UpdateUserNameParams{
		UserName: "alice",
		Password: "new",
		Enabled:  true,
	})

	if err != nil {
		t.Fatal(err)
	}

	if !called {
		t.Fatal("Exec not called")
	}
}
