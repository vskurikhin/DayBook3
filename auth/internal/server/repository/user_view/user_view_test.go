package user_view

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
	data  []UserView
	err   error
}

func (m *mockRows) Next() bool {
	return m.index < len(m.data)
}

func (m *mockRows) Scan(dest ...any) error {
	r := m.data[m.index]
	m.index++

	*(dest[0].(*pgtype.Text)) = r.UserName
	*(dest[1].(*pgtype.UUID)) = r.ID
	*(dest[2].(*pgtype.Text)) = r.Password
	*(dest[3].(*pgtype.Timestamp)) = r.CreateTime
	*(dest[4].(*pgtype.Timestamp)) = r.UpdateTime
	*(dest[5].(*pgtype.Bool)) = r.Enabled
	*(dest[6].(*pgtype.Bool)) = r.LocalChange
	*(dest[7].(*pgtype.Bool)) = r.Visible
	*(dest[8].(*pgtype.Int4)) = r.Flags
	*(dest[9].(*pgtype.Text)) = r.Name
	*(dest[10].(*[]byte)) = r.Attrs
	*(dest[11].(*[]string)) = r.Roles

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
	query func(context.Context, string, ...interface{}) (pgx.Rows, error)
	row   func(context.Context, string, ...interface{}) pgx.Row
	exec  func(context.Context, string, ...interface{}) (pgconn.CommandTag, error)
}

func (m mockDB) Query(ctx context.Context, sql string, args ...interface{}) (pgx.Rows, error) {
	return m.query(ctx, sql, args...)
}

func (m mockDB) QueryRow(ctx context.Context, sql string, args ...interface{}) pgx.Row {
	return m.row(ctx, sql, args...)
}

func (m mockDB) Exec(ctx context.Context, sql string, args ...interface{}) (pgconn.CommandTag, error) {
	return m.exec(ctx, sql, args...)
}

func TestGetUserName(t *testing.T) {
	db := mockDB{
		row: func(ctx context.Context, sql string, args ...interface{}) pgx.Row {
			return mockRow{
				scan: func(dest ...any) error {
					*(dest[0].(*pgtype.Text)) = pgtype.Text{String: "alice", Valid: true}
					*(dest[11].(*[]string)) = []string{"admin", "user"}
					return nil
				},
			}
		},
	}

	q := New(db)

	res, err := q.GetUserName(context.Background(), pgtype.Text{String: "alice", Valid: true})
	if err != nil {
		t.Fatal(err)
	}

	if res.UserName.String != "alice" {
		t.Fatalf("expected alice got %s", res.UserName.String)
	}

	if len(res.Roles) != 2 {
		t.Fatalf("expected 2 roles got %d", len(res.Roles))
	}
}

func TestGetUserName_Error(t *testing.T) {
	db := mockDB{
		row: func(ctx context.Context, sql string, args ...interface{}) pgx.Row {
			return mockRow{
				scan: func(dest ...any) error {
					return errors.New("scan error")
				},
			}
		},
	}

	q := New(db)

	_, err := q.GetUserName(context.Background(), pgtype.Text{})

	if err == nil {
		t.Fatal("expected error")
	}
}

func TestListUserNames(t *testing.T) {
	rows := &mockRows{
		data: []UserView{
			{
				UserName: pgtype.Text{String: "alice", Valid: true},
				Roles:    []string{"admin"},
			},
			{
				UserName: pgtype.Text{String: "bob", Valid: true},
				Roles:    []string{"user"},
			},
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

	if list[0].UserName.String != "alice" {
		t.Fatal("unexpected first user")
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

func TestListUserNames_RowsError(t *testing.T) {
	rows := &mockRows{
		data: []UserView{
			{UserName: pgtype.Text{String: "alice", Valid: true}},
		},
		err: errors.New("rows error"),
	}

	db := mockDB{
		query: func(ctx context.Context, sql string, args ...interface{}) (pgx.Rows, error) {
			return rows, nil
		},
	}

	q := New(db)

	_, err := q.ListUserNames(context.Background())

	if err == nil {
		t.Fatal("expected rows error")
	}
}
