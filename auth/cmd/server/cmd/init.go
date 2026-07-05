// Package cmd provides the CLI entry point and command configuration
// for the application.
//
// The package is built on top of Cobra and Viper and is responsible for:
//
//   - Defining root, run, and migrate subcommands
//   - Registering persistent and local CLI flags
//   - Binding flags to configuration values
//   - Loading configuration from file and environment variables
//   - Initializing application dependencies (config, environment, database)
//
// # Configuration Resolution Order
//
// The application resolves configuration in the following priority order:
//
//  1. Explicit config file passed via --config flag
//  2. Default config file in $HOME directory named ".auth-server.yaml"
//  3. Environment variables (via Viper AutomaticEnv)
//  4. Command-line flags
//
// # Commands
//
// The CLI exposes the following commands:
//
//   - root
//     Base command that defines global flags and configuration.
//
//   - run
//     Starts the application server with provided configuration.
//
//   - migrate
//     Executes database migration-related operations.
//
// # Persistent Flags
//
// The root command defines the following persistent flags:
//
//	--debug, -d
//	    Enables debug mode.
//
//	--verbose, -v
//	    Enables verbose logging output.
//
//	--config
//	    Path to a custom configuration file.
//
// # Run Command Flags
//
// The run command defines:
//
//	--address
//	    Server address in host:port format.
//
//	--https
//	    Enables HTTPS server mode.
//
//	--insecure-skip-verify
//	    Disables TLS certificate verification.
//
//	--hostname
//	    Public hostname of the service.
//
//	--jwt-hs256-sign-key
//	    Secret key used for signing JWT tokens.
//
//	--request-max-bytes
//	    Maximum allowed request body size.
//
//	--valid-period-access-token
//	    Access token expiration duration.
//
//	--valid-period-refresh-token
//	    Refresh token expiration duration.
//
//	--auth-cost
//	    bcrypt cost parameter for password hashing.
//
//	--scheduler-job-sleep-duration
//	    Interval between scheduler job executions.
//
//	--advisory-lock-sleep-duration
//	    Interval between advisory lock acquisition attempts.
//
// # Database Flags
//
// The run and migrate commands define database-related flags:
//
//	--dbhost
//	    Database host.
//
//	--dbport
//	    Database port.
//
//	--dbname
//	    Database name.
//
//	--dbuser
//	    Database username.
//
//	--dbpassword
//	    Database password.
//
//	--dboptions
//	    Additional connection options.
//
//	--db-pool-max-conns
//	    Maximum number of database connections.
//
//	--db-pool-min-conns
//	    Minimum number of database connections.
//
//	--db-pool-max-conn-idle-time
//	    Maximum idle time for connections.
//
//	--db-pool-max-conn-lifetime
//	    Maximum lifetime of a connection.
//
//	--db-pool-health-check-period
//	    Interval for pool health checks.
//
// # Configuration Loading
//
// During initialization, initConfig is automatically executed using
// cobra.OnInitialize. It:
//
//   - Determines the configuration file location
//   - Loads configuration from file if present
//   - Enables automatic environment variable binding
//   - Logs the active configuration file path (if used)
//
// # Dependency Injection Hooks
//
// The package defines overridable function variables:
//
//   - envLoad
//   - newConfig
//   - newDB
//
// These allow replacing default implementations in tests or custom builds.
//
// # Initialization Behavior
//
// This package relies on global Cobra and Viper instances and performs
// initialization in init(), which means:
//
//   - Command tree is constructed at import time
//   - Flags are registered automatically
//   - Configuration bootstrap is pre-wired
//
// # Notes
//
// For advanced use cases (such as testing, embedding, or modular CLI design),
// consider refactoring into a constructor-based command builder instead of
// relying on global state and init() side effects.
package cmd

import (
	"context"
	"log/slog"
	"os"
	"time"

	"github.com/spf13/cobra"
	"github.com/spf13/viper"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/db"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/env"
)

const (
	FlagAddress                   = "address"
	FlagAdvisoryLockSleepDuration = "advisory-lock-sleep-duration"
	FlagAuthCost                  = "auth-cost"
	FlagDBHost                    = "dbhost"
	FlagDBName                    = "dbname"
	FlagDBOptions                 = "dboptions"
	FlagDBPassword                = "dbpassword"
	FlagDBPoolHealthCheckPeriod   = "db-pool-health-check-period"
	FlagDBPoolMaxConnIdleTime     = "db-pool-max-conn-idle-time"
	FlagDBPoolMaxConnLifeTime     = "db-pool-max-conn-lifetime"
	FlagDBPoolMaxConns            = "db-pool-max-conns"
	FlagDBPoolMinConns            = "db-pool-min-conns"
	FlagDBPort                    = "dbport"
	FlagDBUser                    = "dbuser"
	FlagDebug                     = "debug"
	FlagHostname                  = "hostname"
	FlagInsecureSkipVerify        = "insecure-skip-verify"
	FlagIsHTTPS                   = "https"
	FlagJWThs256SignKey           = "jwt-hs256-sign-key"
	FlagRequestMaxBytes           = "request-max-bytes"
	FlagSchedulerJobSleepDuration = "scheduler-job-sleep-duration"
	FlagServerCertFile            = "server-cert-file"
	FlagServerKeyFile             = "server-key-file"
	FlagValidPeriodAccessToken    = "valid-period-access-token"
	FlagValidPeriodRefreshToken   = "valid-period-refresh-token"
	FlagVerbose                   = "verbose"
)

var (
	envLoad   = func() (env.Environments, error) { return env.EnvironmentsLoad() }
	newConfig = func(cmd *cobra.Command) (config.Config, error) { return config.NewConfig(cmd) }
	newDB     = func(ctx context.Context, cfg config.Config, env env.Environments) (db.DB, error) {
		return db.NewDB(ctx, cfg, env)
	}
)

func init() {
	cobra.OnInitialize(initConfig)

	// Here you will define your flags and configuration settings.
	// Cobra supports persistent flags, which, if defined here,
	// will be global for your application.

	rootCmd.PersistentFlags().StringVar(&cfgFile, "config", "", "Config file (default is $HOME/.auth-server.yaml)")

	// Cobra also supports local flags, which will only run
	// when this action is called directly.
	rootCmd.PersistentFlags().BoolP(FlagDebug, "d", false, "Help message for debug.")
	rootCmd.PersistentFlags().BoolP(FlagVerbose, "v", false, "Verbose.")

	runCmd.Flags().Bool(FlagInsecureSkipVerify, false, "Controls whether a client verifies the server's certificate chain and host name.")
	runCmd.Flags().Bool(FlagIsHTTPS, false, "Controls whether a server's HTTPS bindings.")
	runCmd.Flags().Duration(FlagAdvisoryLockSleepDuration, time.Minute, "Advisory lock sleep duration.")
	runCmd.Flags().Duration(FlagDBPoolHealthCheckPeriod, time.Minute, "Pgx DB pool health check period.")
	runCmd.Flags().Duration(FlagDBPoolMaxConnIdleTime, 30*time.Minute, "Pgx DB pool max connection idle time.")
	runCmd.Flags().Duration(FlagDBPoolMaxConnLifeTime, time.Hour, "Pgx DB pool max connection lifetime.")
	runCmd.Flags().Duration(FlagValidPeriodAccessToken, 257*time.Second, "Valid period access token.")
	runCmd.Flags().Duration(FlagValidPeriodRefreshToken, 7*time.Hour, "Valid period refresh token.")
	runCmd.Flags().Duration(FlagSchedulerJobSleepDuration, 5*time.Second, "Scheduler job sleep duration.")
	runCmd.Flags().String(FlagAddress, "127.0.0.1:8089", "Address as host:port")
	runCmd.Flags().String(FlagDBHost, "localhost", "Pgx pool DB host.")
	runCmd.Flags().String(FlagDBName, "db", "Pgx pool DB name.")
	runCmd.Flags().String(FlagDBOptions, "application_name=auth&search_path=auth", "Pgx pool DB options.")
	runCmd.Flags().String(FlagDBPassword, "password", "Pgx pool DB user password.")
	runCmd.Flags().String(FlagDBUser, "dbuser", "Pgx pool DB username.")
	runCmd.Flags().String(FlagHostname, "localhost", "Hostname.")
	runCmd.Flags().String(FlagJWThs256SignKey, "", "JWT HS256 signing key.")
	runCmd.Flags().String(FlagServerCertFile, "cert.pem", "Server certificate file.")
	runCmd.Flags().String(FlagServerKeyFile, "key.pem", "Server certificate key file.")
	runCmd.Flags().Uint16(FlagDBPort, 5432, "Pgx pool DB port.")
	runCmd.Flags().Uint64(FlagRequestMaxBytes, 1<<20, "Request max bytes.")
	runCmd.Flags().Uint8(FlagAuthCost, 14, "The minimum allowable cost as passed in")
	runCmd.Flags().Uint8(FlagDBPoolMaxConns, 4, "Pgx DB pool max connections.")
	runCmd.Flags().Uint8(FlagDBPoolMinConns, 0, "Pgx DB pool min connections.")

	migrateCmd.Flags().String(FlagDBHost, "localhost", "Pgx pool DB host.")
	migrateCmd.Flags().String(FlagDBName, "db", "Pgx pool DB name.")
	migrateCmd.Flags().String(FlagDBOptions, "application_name=auth&search_path=auth", "Pgx pool DB options.")
	migrateCmd.Flags().String(FlagDBPassword, "password", "Pgx pool DB user password.")
	migrateCmd.Flags().Uint16(FlagDBPort, 5432, "Pgx pool DB port.")
	migrateCmd.Flags().String(FlagDBUser, "dbuser", "Pgx pool DB username.")

	rootCmd.AddCommand(runCmd)
	rootCmd.AddCommand(migrateCmd)
}

// initConfig reads in Config file and ENV variables if set.
func initConfig() {
	if cfgFile != "" {
		// Use Config file from the flag.
		viper.SetConfigFile(cfgFile)
	} else {
		// Find home directory.
		home, err := os.UserHomeDir()
		cobra.CheckErr(err)

		// Search Config in home directory with name ".auth-server" (without extension).
		viper.AddConfigPath(home)
		viper.SetConfigType("yaml")
		viper.SetConfigName(".auth-server")
	}
	viper.AutomaticEnv() // read in environment variables that match

	// If a Config file is found, read it in.
	if err := viper.ReadInConfig(); err == nil {
		slog.Info("Using Config", slog.String("file", viper.ConfigFileUsed()))
	}
}
