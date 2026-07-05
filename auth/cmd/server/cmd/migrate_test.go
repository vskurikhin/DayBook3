package cmd

import (
	"context"
	"database/sql"
	"errors"
	"testing"

	"github.com/DATA-DOG/go-sqlmock"
	_ "github.com/jackc/pgx/v5/stdlib"
	"github.com/spf13/cobra"
	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
)

func newTestMergeCommand() *cobra.Command {
	cmd := &cobra.Command{
		Use: "test",
	}

	cmd.Flags().Bool("debug", false, "")
	cmd.Flags().Bool("enabled", false, "")
	cmd.Flags().Bool("verbose", false, "")
	cmd.Flags().String("dbhost", "127.0.0.1", "")
	cmd.Flags().String("dbname", "dbname", "")
	cmd.Flags().String("dbpassword", "dbpassword", "")
	cmd.Flags().Uint16("dbport", 65432, "")
	cmd.Flags().String("dbuser", "user", "")
	cmd.Flags().String("name", "default", "")
	cmd.SetContext(context.Background())

	return cmd
}

func newTestMergeErrorCommand() *cobra.Command {
	cmd := &cobra.Command{
		Use: "test",
	}

	cmd.Flags().Bool("debug", false, "")
	cmd.Flags().Bool("enabled", false, "")
	cmd.Flags().Bool("verbose", false, "")
	cmd.Flags().String("dbhost", "", "")
	cmd.Flags().String("dbname", "", "")
	cmd.Flags().String("dbpassword", "", "")
	cmd.Flags().Uint16("dbport", 0, "")
	cmd.Flags().String("dbuser", "", "")
	cmd.Flags().String("name", "default", "")
	cmd.SetContext(context.Background())

	return cmd
}

func TestMigrateCmd_ConfigError(t *testing.T) {
	// backup originals
	origNewConfig := newConfig
	defer func() {
		newConfig = origNewConfig
	}()

	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockConfig := NewMockConfig(ctrl)
	mockConfig.EXPECT().Values().Return(testDefaultConfigValues).AnyTimes()

	newConfig = func(cmd *cobra.Command) (config.Config, error) {
		return mockConfig, errors.New("config error")
	}

	err := migrateCmd.RunE(newTestMergeCommand(), nil)

	if err == nil {
		t.Fatal("expected error")
	}
}

var testConnectionStringBuilderErrorConfigValues = config.Values{
	DBHost: "",
}

func TestMigrateCmd_ConnectionStringBuilderError(t *testing.T) {
	// backup originals
	origNewConfig := newConfig
	defer func() {
		newConfig = origNewConfig
	}()

	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockConfig := NewMockConfig(ctrl)
	mockConfig.EXPECT().Values().Return(testConnectionStringBuilderErrorConfigValues).AnyTimes()

	newConfig = func(cmd *cobra.Command) (config.Config, error) {
		return mockConfig, nil
	}

	err := migrateCmd.RunE(newTestMergeErrorCommand(), nil)

	if err == nil {
		t.Fatal("expected dbuser empty error")
	}
}

func TestMigrateCmd_SqlOpenError(t *testing.T) {
	// backup originals
	origNewConfig := newConfig
	defer func() {
		newConfig = origNewConfig
	}()

	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockConfig := NewMockConfig(ctrl)
	mockConfig.EXPECT().Values().Return(testDefaultConfigValues).AnyTimes()

	newConfig = func(cmd *cobra.Command) (config.Config, error) {
		return mockConfig, nil
	}

	sqlOpen = func(dataSourceName string) (*sql.DB, error) {
		db, _, _ := sqlmock.New()
		return db, errors.New("open error")
	}

	err := migrateCmd.RunE(newTestMergeCommand(), nil)

	if err == nil {
		t.Fatal("expected sql open error")
	}
}

func TestMigrateCmd_SetDialectError(t *testing.T) {
	// backup originals
	origNewConfig := newConfig
	defer func() {
		newConfig = origNewConfig
	}()

	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockConfig := NewMockConfig(ctrl)
	mockConfig.EXPECT().Values().Return(testDefaultConfigValues).AnyTimes()

	newConfig = func(cmd *cobra.Command) (config.Config, error) {
		return mockConfig, nil
	}

	sqlOpen = func(dataSourceName string) (*sql.DB, error) {
		db, _, err := sqlmock.New()
		return db, err
	}

	oldDialect := dialect
	defer func() { dialect = oldDialect }()
	dialect = "unknown dialect"

	err := migrateCmd.RunE(newTestMergeCommand(), nil)

	if err == nil {
		t.Fatal("expected sql open error")
	}
}

func TestMigrateCmd_GooseUpError(t *testing.T) {
	// backup originals
	origNewConfig := newConfig
	defer func() {
		newConfig = origNewConfig
	}()

	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockConfig := NewMockConfig(ctrl)
	mockConfig.EXPECT().Values().Return(testDefaultConfigValues).AnyTimes()

	newConfig = func(cmd *cobra.Command) (config.Config, error) {
		return mockConfig, nil
	}

	sqlOpen = func(dataSourceName string) (*sql.DB, error) {
		db, _, err := sqlmock.New()
		return db, err
	}

	gooseUp = func(db *sql.DB) error {
		return errors.New("migration error")
	}

	err := migrateCmd.RunE(newTestMergeCommand(), nil)

	if err == nil {
		t.Fatal("expected goose up error")
	}
}

func TestMigrateCmd_Success(t *testing.T) {
	// backup originals
	origNewConfig := newConfig
	defer func() {
		newConfig = origNewConfig
	}()

	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockConfig := NewMockConfig(ctrl)
	mockConfig.EXPECT().Values().Return(testDefaultConfigValues).AnyTimes()

	newConfig = func(cmd *cobra.Command) (config.Config, error) {
		return mockConfig, nil
	}

	sqlOpen = func(dataSourceName string) (*sql.DB, error) {
		db, _, err := sqlmock.New()
		return db, err
	}

	gooseUp = func(db *sql.DB) error {
		return nil
	}

	err := migrateCmd.RunE(newTestMergeCommand(), nil)

	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
}
