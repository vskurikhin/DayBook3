package cmd

import (
	"fmt"
	"log/slog"
	"strconv"
	"time"

	"github.com/spf13/cobra"
	"github.com/spf13/pflag"
	"github.com/spf13/viper"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/env"
	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

func mergeCobraToViper(cmd *cobra.Command) {
	for key := range viper.GetViper().AllSettings() {
		viperBindPFlag(key, tool.SnakeCaseToKebabCase(key), cmd)
	}
	cmd.Flags().VisitAll(mergeCobraToViperFunc(cmd))
}

func mergeCobraToViperFunc(cmd *cobra.Command) func(f *pflag.Flag) {
	return func(f *pflag.Flag) {
		if tool.IsDebug(cmd) {
			slog.Debug(
				"variable:", "flag",
				fmt.Sprintf("FLAG: (Type: %s, Changed: %v) --%s=%q",
					f.Value.Type(), f.Changed, f.Name, f.Value.String(),
				))
		}
		if f.Changed || viper.Get(tool.KebabCaseToSnakeCase(f.Name)) == nil {
			switch f.Value.Type() {
			case "bool":
				viper.Set(tool.KebabCaseToSnakeCase(f.Name), f.Value.String() == "true")
			case "duration":
				value, err := time.ParseDuration(f.Value.String())
				if err == nil {
					viper.Set(tool.KebabCaseToSnakeCase(f.Name), value)
				}
			case "int", "int8", "int16", "int32", "int64", "uint", "uint8", "uint16", "uint32", "uint64":
				value, _ := strconv.Atoi(f.Value.String())
				if value > 0 {
					viper.Set(tool.KebabCaseToSnakeCase(f.Name), value)
				} else {
					value, _ = strconv.Atoi(f.DefValue)
					viper.Set(tool.KebabCaseToSnakeCase(f.Name), value)
				}
			case "string":
				viper.Set(tool.KebabCaseToSnakeCase(f.Name), f.Value.String())
			}
		}
	}
}

func mergeEnvToConfig(cfg config.Config, env env.Environments) {
	if cfg == nil {
		return
	}
	if env == nil {
		return
	}
	if string(cfg.Values().JWThs256SignKey) == "" && env.Values().JWThs256SignKey != "" {
		cfg.JWThs256SignKey(env.Values().JWThs256SignKey)
	}
}

func viperBindPFlag(key, flag string, cmd *cobra.Command) {
	err := viper.BindPFlag(key, cmd.Flags().Lookup(flag))
	if err != nil && tool.IsDebug(cmd) {
		slog.Error("Error binding flag", "error", err)
	}
}

func setSlogDebug(cmd *cobra.Command) {
	if tool.IsDebug(cmd) {
		slog.SetLogLoggerLevel(slog.LevelDebug)
	}
}
