package cmd

import (
	"context"
	"errors"

	"github.com/spf13/cobra"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/actions"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services"
	"github.com/vskurikhin/DayBook3/auth/v2/cmd/server/wire"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/env"
)

//go:generate mockgen -destination=run_mock_config_test.go -package=cmd github.com/vskurikhin/DayBook3/auth/v2/cmd/server/cmd Config
type Config interface {
	JWThs256SignKey(string)
	Values() config.Values
}

//go:generate mockgen -destination=run_mock_environments_test.go -package=cmd github.com/vskurikhin/DayBook3/auth/v2/cmd/server/cmd Environments
type Environments interface {
	Values() env.Values
}

//go:generate mockgen -destination=run_mock_server_test.go -package=cmd github.com/vskurikhin/DayBook3/auth/v2/internal/server Server
type Server interface {
	Run(ctx context.Context) error
}

var (
	ErrDBPoolIsNil = errors.New("db pool is nil")
	newAuthServer  = func(cfg config.Config, env env.Environments) (server.Server, error) {
		return wire.InitializeServer(cfg, env)
	}
)

// runCmd represents the base command when called without any subcommands
var runCmd = &cobra.Command{
	Use:   "run",
	Short: "Starts the authentication server",
	Long: `The "run" command launches the authentication server of the application.
It initializes the configuration by reading values from CLI flags, a configuration file,
or environment variables, sets the logging level, and starts the main server
using the obtained configuration.`,
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

		env, errEnvLoad := envLoad()
		if errEnvLoad != nil {
			return errEnvLoad
		}
		mergeEnvToConfig(cfg, env)

		dbp, errNewDB := newDB(cmd.Context(), cfg, env)
		if errNewDB != nil {
			return errNewDB
		}
		if dbp == nil {
			return ErrDBPoolIsNil
		}

		errRunScheduler := services.RunScheduler(cmd.Context(), cfg, dbp, (&actions.SessionCleaner{}).Clean)
		if errRunScheduler != nil {
			return errRunScheduler
		}

		srv, err := newAuthServer(cfg, env)
		if err != nil {
			return err
		}
		return srv.Run(cmd.Context())
	},
}
