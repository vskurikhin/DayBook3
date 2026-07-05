package cmd

import (
	"context"
	"log/slog"

	"github.com/spf13/cobra"
	"github.com/spf13/viper"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

const (
	ExitCodeOK int = iota * 100
	ExitCodeContextDone
	ExitCodeError
)

var (
	cfgFile    string
	executeCmd = rootCmd.ExecuteContext
)

// rootCmd represents the base command when called without any subcommands
var rootCmd = &cobra.Command{
	Use:   "auth-server",
	Short: "Authentication server for DayBook application",
	Long: `The "auth-server" command is the entry point for the authentication service
of the DayBook application.

It provides subcommands to run the server, manage configuration, and
perform administrative tasks. Configuration can be supplied via CLI flags,
a configuration file, or environment variables.

Typical usage:

  # Run the server with default configuration
  auth-server run

  # Run the server with custom config file
  auth-server --config /path/to/config.yaml run

All commands support standard flags such as --debug, --verbose, and --config.`,
	// Uncomment the following line if your bare application
	// has an action associated with it:
	RunE: func(cmd *cobra.Command, args []string) error {
		setSlogDebug(cmd)
		mergeCobraToViper(cmd)
		slogInfoVerbose(cmd)
		_, err := config.NewConfig(cmd)
		return err
	},
}

func slogInfoVerbose(cmd *cobra.Command) {
	if tool.IsVerbose(cmd) {
		for key, value := range viper.GetViper().AllSettings() {
			slog.Info("variable:", key, value)
		}
	}
}

// Execute adds all child commands to the root command and sets flags appropriately.
// This is called by main.main(). It only needs to happen once to the rootCmd.
func Execute(ctx context.Context) int {
	var err error
	done := make(chan struct{})
	go func() {
		err = executeCmd(ctx)
		close(done)
	}()
	for {
		select {
		case <-ctx.Done():
			slog.Error("App ctx.Done", "error", ctx.Err())
			return ExitCodeContextDone
		case <-done:
			if err != nil {
				slog.Error("App Error", slog.String("error", err.Error()))
				return ExitCodeError
			}
			slog.Info("App Done")
			return ExitCodeOK
		}
	}
}
