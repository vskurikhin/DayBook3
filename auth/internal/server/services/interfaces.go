package services

import (
	"context"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgtype"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_attrs"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_has_roles"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_name"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_view"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
)

//go:generate mockgen -destination=mock_config_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services Config
type Config interface {
	JWThs256SignKey(string)
	Values() config.Values
}

//go:generate mockgen -destination=mock_credentials_factory_v2_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services CredentialsFactoryV2
type CredentialsFactoryV2 interface {
	MakeCredentials(credValues model.CredValuesV2, err error) (model.Credentials, error)
}

//go:generate mockgen -destination=mock_db_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services DB
type DB interface {
	Acquire(ctx context.Context) (c *pgxpool.Conn, err error)
	Begin(ctx context.Context) (pgx.Tx, error)
	Exec(ctx context.Context, sql string, arguments ...interface{}) (pgconn.CommandTag, error)
	Query(ctx context.Context, sql string, optionsAndArgs ...interface{}) (pgx.Rows, error)
	QueryRow(ctx context.Context, sql string, optionsAndArgs ...interface{}) pgx.Row
}

//go:generate mockgen -destination=mock_dbtx_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services DBTX
type DBTX interface {
	Exec(context.Context, string, ...interface{}) (pgconn.CommandTag, error)
	Query(context.Context, string, ...interface{}) (pgx.Rows, error)
	QueryRow(context.Context, string, ...interface{}) pgx.Row
}

//go:generate mockgen -destination=mock_pgx_conn_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services PgxConn
type PgxConn interface {
	QueryRow(ctx context.Context, sql string, args ...any) pgx.Row
	Exec(ctx context.Context, sql string, args ...any) (pgconn.CommandTag, error)
	Release()
}

//go:generate mockgen -destination=mock_row_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services Row
type Row interface {
	Scan(dest ...any) error
}

//go:generate mockgen -destination=mock_rows_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services Rows
type Rows interface {
	Close()
	Err() error
	CommandTag() pgconn.CommandTag
	FieldDescriptions() []pgconn.FieldDescription
	Next() bool
	Scan(dest ...any) error
	Values() ([]any, error)
	RawValues() [][]byte
	Conn() *pgx.Conn
}

//go:generate mockgen -destination=mock_session_repo_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services SessionRepo
type SessionRepo interface {
	CreateSession(ctx context.Context, arg session.CreateSessionParams) (session.Session, error)
	DeleteSession(ctx context.Context, arg session.DeleteSessionParams) error
	GetSession(ctx context.Context, arg session.GetSessionParams) (session.Session, error)
	ListSessions(ctx context.Context) ([]session.Session, error)
	UpdateSession(ctx context.Context, arg session.UpdateSessionParams) error
	WithTx(tx pgx.Tx) *session.Queries
}

//go:generate mockgen -destination=mock_tx_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services Tx
type Tx interface {
	Begin(ctx context.Context) (pgx.Tx, error)
	Commit(ctx context.Context) error
	Rollback(ctx context.Context) error
	CopyFrom(ctx context.Context, tableName pgx.Identifier, columnNames []string, rowSrc pgx.CopyFromSource) (int64, error)
	SendBatch(ctx context.Context, b *pgx.Batch) pgx.BatchResults
	LargeObjects() pgx.LargeObjects
	Prepare(ctx context.Context, name, sql string) (*pgconn.StatementDescription, error)
	Exec(ctx context.Context, sql string, arguments ...any) (commandTag pgconn.CommandTag, err error)
	Query(ctx context.Context, sql string, args ...any) (pgx.Rows, error)
	QueryRow(ctx context.Context, sql string, args ...any) pgx.Row
	Conn() *pgx.Conn
}

//go:generate mockgen -destination=mock_tx_delayer_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services TxDelayer
type TxDelayer interface {
	Defer(ctx context.Context, tx pgx.Tx, err error)
}

//go:generate mockgen -destination=mock_user_attrs_repo_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services UserAttrsRepo
type UserAttrsRepo interface {
	CreateUserAttrs(ctx context.Context, arg user_attrs.CreateUserAttrsParams) (user_attrs.UserAttr, error)
	DeleteUserAttrs(ctx context.Context, userName string) error
	GetUserAttrs(ctx context.Context, userName string) (user_attrs.UserAttr, error)
	ListUserAttrs(ctx context.Context) ([]user_attrs.UserAttr, error)
	UpdateUserAttrs(ctx context.Context, arg user_attrs.UpdateUserAttrsParams) error
	WithTx(tx pgx.Tx) *user_attrs.Queries
}

//go:generate mockgen -destination=mock_user_has_roles_repo_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services UserHasRolesRepo
type UserHasRolesRepo interface {
	CreateUserHasRoles(ctx context.Context, arg user_has_roles.CreateUserHasRolesParams) (user_has_roles.UserHasRole, error)
	DeleteUserHasRolesBy(ctx context.Context, userName string) error
	DeleteUserHasRolesByID(ctx context.Context, id int64) error
	GetRolesForUserName(ctx context.Context, userName string) (user_has_roles.GetRolesForUserNameRow, error)
	ListUserHasRoles(ctx context.Context) ([]user_has_roles.UserHasRole, error)
	ListUserHasRolesByRole(ctx context.Context, role string) ([]user_has_roles.UserHasRole, error)
	ListUserHasRolesByUserName(ctx context.Context, userName string) ([]user_has_roles.UserHasRole, error)
	UpdateUserHasRoles(ctx context.Context, arg user_has_roles.UpdateUserHasRolesParams) error
	WithTx(tx pgx.Tx) *user_has_roles.Queries
}

//go:generate mockgen -destination=mock_user_name_repo_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services UserNameRepo
type UserNameRepo interface {
	CreateUserName(ctx context.Context, arg user_name.CreateUserNameParams) (user_name.UserName, error)
	DeleteUserNameByID(ctx context.Context, id pgtype.UUID) error
	DeleteUserNameByName(ctx context.Context, userName string) error
	GetUserName(ctx context.Context, userName string) (user_name.UserName, error)
	ListUserNames(ctx context.Context) ([]user_name.UserName, error)
	UpdateUserName(ctx context.Context, arg user_name.UpdateUserNameParams) error
	WithTx(tx pgx.Tx) *user_name.Queries
}

//go:generate mockgen -destination=mock_user_view_repo_test.go -package=services github.com/vskurikhin/DayBook3/auth/v2/internal/server/services UserViewRepo
type UserViewRepo interface {
	GetUserName(ctx context.Context, userName pgtype.Text) (user_view.UserView, error)
	ListUserNames(ctx context.Context) ([]user_view.UserView, error)
}
