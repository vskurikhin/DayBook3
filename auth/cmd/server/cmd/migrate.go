package cmd

import (
	"database/sql"
	"embed"
	"strconv"

	_ "github.com/jackc/pgx/v5/stdlib"
	"github.com/pressly/goose/v3"
	"github.com/spf13/cobra"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/db"
)

const (
	AuthGooseDbVersion = "auth.goose_db_version"
	Migrations         = "migrations"
	Pgx                = "pgx"
	Postgres           = "postgres"
)

var (
	dialect = Postgres
	gooseUp = func(db *sql.DB) error { return goose.Up(db, Migrations) }
	sqlOpen = func(connString string) (*sql.DB, error) { return sql.Open(Pgx, connString) }
)

//go:embed migrations/*.sql
var embedMigrations embed.FS

// migrateCmd TODO
var migrateCmd = &cobra.Command{
	Use:   "migrate",
	Short: "Apply migrations to the database",
	Long: `The "migrate" execute a PostgreSQL migrations and applies all pending migrations.
Migrations are executed using the migration tool with the configured version table.`,
	// The following line your bare application
	// has an action associated with it:
	RunE: func(cmd *cobra.Command, args []string) error {
		setSlogDebug(cmd)
		mergeCobraToViper(cmd)
		slogInfoVerbose(cmd)

		cfg, errConfig := newConfig(cmd)
		if errConfig != nil {
			return errConfig
		}

		builder := db.ConstructConnectionStringBuilder(
			db.WithDBUser(cfg.Values().DBUser),
			db.WithDBPassword(cfg.Values().DBPassword),
			db.WithDBHost(cfg.Values().DBHost),
			db.WithDBPort(strconv.Itoa(int(cfg.Values().DBPort))),
			db.WithDBName(cfg.Values().DBName),
		).WithDBOptions(cfg.Values().DBOptions)
		connString, errBuild := builder.Build()
		if errBuild != nil {
			return errBuild
		}
		db, errDBOpen := sqlOpen(connString)
		defer func() { _ = db.Close() }()
		if errDBOpen != nil {
			return errDBOpen
		}
		goose.SetBaseFS(embedMigrations)
		goose.SetTableName(AuthGooseDbVersion)

		if errSetDialect := goose.SetDialect(dialect); errSetDialect != nil {
			return errSetDialect
		}

		if err := gooseUp(db); err != nil {
			return err
		}
		return nil
	},
}
