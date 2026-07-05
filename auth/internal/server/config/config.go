// Package config provides functionality for configuring the DayBook
// authentication server.
//
// Configuration is loaded using Viper and can be sourced from:
//
//   - CLI flags (via Cobra)
//   - Configuration files (e.g. YAML)
//   - Environment variables
//
// The package implements a thread-safe singleton pattern for storing
// global application configuration and exposes it via the Config interface.
//
// # Configuration Model
//
// The configuration is represented by the Values struct, which contains
// all runtime settings such as:
//
//   - Server settings (Address, HTTPS, Hostname)
//   - Security settings (JWT keys, TLS, auth cost)
//   - Database connection and pool settings
//   - Scheduler timings
//   - Logging options (Debug, Verbose)
//
// The Config interface provides controlled access to these values.
//
// # Singleton Behavior
//
// Configuration is initialized only once using sync.Once in NewConfig.
// All subsequent calls return the same instance.
//
// Internally:
//   - ValuesConfig stores the configuration
//   - sync.RWMutex ensures thread-safe read/write access
//
// # Dynamic Configuration Reloading
//
// The package supports runtime configuration reloading in two ways:
//
//  1. File system notifications via fsnotify (viper.WatchConfig)
//  2. OS signal handling (SIGHUP)
//
// When a configuration change is detected:
//   - The config file is re-read
//   - Values are unmarshaled into a new struct
//   - The global configuration is updated atomically
//
// This allows updating configuration without restarting the application.
//
// # Concurrency Guarantees
//
//   - Reads are protected by RLock
//   - Writes (reloads) are protected by Lock
//   - Values() returns a copy of the configuration
//
// # Security Notes
//
//   - JWThs256SignKey is stored as a byte slice
//   - Sensitive fields (e.g., passwords) are loaded but not masked automatically
//   - The ssl field is unexported and accessible only via Ssl()
//
// # Dependency on Viper
//
// The package relies on Viper for:
//
//   - Unmarshalling configuration into structs
//   - Watching file changes
//   - Environment variable binding
//
// # Functions
//
// NewConfig(cmd *cobra.Command) (*ValuesConfig, error)
//
//	Initializes and returns the singleton configuration instance.
//	Also starts background goroutines for config reload.
//
// GetConfig() *ValuesConfig
//
//	Returns the existing configuration instance (initializing it if needed).
//
// # Runtime Behavior
//
// On initialization:
//
//   - Configuration is unmarshaled from Viper
//   - Debug logging may print full config (if enabled)
//   - File watching is started
//   - SIGHUP listener is started in a background goroutine
//
// # Signal Handling
//
// The package listens for syscall.SIGHUP:
//
//   - On receiving SIGHUP, configuration is reloaded from file
//   - Errors during reload are logged but do not stop the application
//
// # Example Usage
//
//	cmd := &cobra.Command{}
//	cmd.Flags().Bool("debug", false, "enable debug mode")
//
//	cfg, err := config.NewConfig(cmd)
//	if err != nil {
//	    panic(err)
//	}
//
//	values := cfg.Values()
//	fmt.Println(values.Address)
//
//	if values.Ssl() {
//	    fmt.Println("SSL enabled")
//	}
package config

import (
	"context"
	"fmt"
	"log/slog"
	"os"
	"os/signal"
	"sync"
	"syscall"
	"time"

	"github.com/fsnotify/fsnotify"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"

	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

//go:generate mockgen -destination=config_mock_test.go -package=config github.com/vskurikhin/DayBook3/auth/v2/internal/server/config Config
type Config interface {
	JWThs256SignKey(string)
	Values() Values
}

type Values struct {
	Address                   string        `mapstructure:"address"`
	AdvisoryLockSleepDuration time.Duration `mapstructure:"advisory_lock_sleep_duration"`
	AuthCost                  uint8         `mapstructure:"auth_cost"`

	DBHost     string `mapstructure:"dbhost"`
	DBName     string `mapstructure:"dbname"`
	DBOptions  string `mapstructure:"dboptions"`
	DBPassword string `mapstructure:"dbpassword"`
	DBPort     uint16 `mapstructure:"dbport"`
	DBUser     string `mapstructure:"dbuser"`

	DBPoolMaxConns          int           `mapstructure:"db_pool_max_conns"`
	DBPoolMinConns          int           `mapstructure:"db_pool_min_conns"`
	DBPoolMaxConnLifeTime   time.Duration `mapstructure:"db_pool_max_conn_lifetime"`
	DBPoolMaxConnIdleTime   time.Duration `mapstructure:"db_pool_max_conn_idle_time"`
	DBPoolHealthCheckPeriod time.Duration `mapstructure:"db_pool_health_check_period"`

	Debug                     bool          `mapstructure:"debug"`
	HTTPS                     bool          `mapstructure:"https"`
	Hostname                  string        `mapstructure:"hostname"`
	InsecureSkipVerify        bool          `mapstructure:"insecure_skip_verify"`
	JWThs256SignKey           []byte        `mapstructure:"jwt_hs256_sign_key"`
	RequestMaxBytes           uint64        `mapstructure:"request_max_bytes"`
	SchedulerJobSleepDuration time.Duration `mapstructure:"scheduler_job_sleep_duration"`
	ServerCertFile            string        `mapstructure:"server_cert_file"`
	ServerKeyFile             string        `mapstructure:"server_key_file"`
	ValidPeriodAccessToken    time.Duration `mapstructure:"valid_period_access_token"`
	ValidPeriodRefreshToken   time.Duration `mapstructure:"valid_period_refresh_token"`
	Verbose                   bool          `mapstructure:"verbose"`
	ssl                       bool
}

var _ Config = (*ValuesConfig)(nil)

type ValuesConfig struct {
	values Values
	mu     sync.RWMutex
}

func (v *ValuesConfig) JWThs256SignKey(s string) {
	v.mu.Lock()
	defer v.mu.Unlock()
	v.values.JWThs256SignKey = []byte(s)
}

// Values returns the configuration values stored in the Config instance.
//
// This method provides read-only access to the underlying Values struct,
// which contains all the application configuration fields such as Address,
// Debug, InsecureSkipVerify, and Verbose.
//
// Example:
//
//	cfg, err := config.NewConfig(cmd)
//	if err == nil {
//		values := cfg.Values()
//		fmt.Println(values.Address)
//		if values.Debug {
//			fmt.Println("Debug mode enabled")
//		}
//	}
func (v *ValuesConfig) Values() Values {
	v.mu.RLock()
	defer v.mu.RUnlock()
	return v.values
}

var (
	cfg  *ValuesConfig
	once = new(sync.Once)
)

// NewConfig creates and returns a singleton instance of ValuesConfig.
//
// It reads configuration values from Viper and the provided Cobra command.
// If the "debug" flag is enabled on the command, any errors encountered
// during unmarshalling will be logged, and the loaded configuration will
// also be printed for debugging purposes.
//
// The returned ValuesConfig implements the Config interface and provides
// access to the configuration via the Values() method.
//
// This function uses sync.Once to ensure that only one instance of
// ValuesConfig is created (singleton pattern).
//
// Example usage:
//
//	 cmd := &cobra.Command{}
//	 cmd.Flags().Bool("debug", false, "enable debug mode")
//	 cfg, err := config.NewConfig(cmd)
//	 if err == nil {
//		fmt.Println(cfg.Values().Address)
//	 }
func NewConfig(cmd *cobra.Command) (*ValuesConfig, error) {
	var v, values Values
	err := viper.Unmarshal(&v)
	if err != nil {
		if cmd != nil && tool.IsDebug(cmd) {
			slog.Error("Error binding flag", "error", err)
		}
		return nil, err
	} else {
		values = v
	}
	if cmd != nil && tool.IsDebug(cmd) {
		slog.Debug("variable:", "ValuesConfig", fmt.Sprintf("%+v", values))
	}
	vc := ValuesConfig{}
	vc.mu.Lock()
	defer vc.mu.Unlock()
	once.Do(func() {
		vc.values = values
		cfg = &vc
		go loopSigHup(context.Background())
		viper.WatchConfig()
		viper.OnConfigChange(configChange)
	})
	return cfg, err
}

func GetConfig() *ValuesConfig {
	c, _ := NewConfig(nil)
	return c
}

// Ssl returns the value of the internal ssl field.
//
// This indicates whether SSL/TLS is enabled for the configuration.
// It is a read-only accessor.
func (c Values) Ssl() bool {
	return c.ssl
}

func configChange(_ fsnotify.Event) {
	slog.Info("Config file changed, reloading...")
	var newConfig Values
	if err := viper.Unmarshal(&newConfig); err != nil {
		slog.Error("Error unmarshaling config", "error", err)
		return
	}
	cfg.mu.Lock()
	defer cfg.mu.Unlock()
	cfg.values = newConfig
}

func loopSigHup(ctx context.Context) {
	sigHup := make(chan os.Signal, 1)
	signal.Notify(sigHup, syscall.SIGHUP)
	for {
		select {
		case <-ctx.Done():
			return
		case <-sigHup:
			// If a Config file is found, read it in.
			if err := viper.ReadInConfig(); err == nil {
				slog.Info("Config file changed, reloading...", slog.String("file", viper.ConfigFileUsed()))
				var newConfig Values
				if err := viper.Unmarshal(&newConfig); err != nil {
					slog.Error("Error unmarshaling config", "error", err)
					continue
				}
				cfg.mu.Lock()
				cfg.values = newConfig
				cfg.mu.Unlock()
			} else {
				slog.Info(
					"Config file changed, reload fail",
					slog.String("file", viper.ConfigFileUsed()),
					slog.String("error", err.Error()),
				)
			}
		}
	}
}
