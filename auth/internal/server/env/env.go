// Package env provides loading and access to application configuration
// defined via environment variables.
//
// The package is responsible for reading environment variables at startup
// and converting them into a strongly typed configuration structure.
//
// Configuration values are parsed using the
// github.com/caarlos0/env/v11 library, which maps environment variables
// to struct fields via struct tags and supports default values.
//
// # Design Overview
//
// The package defines a small abstraction layer via the Environments interface,
// allowing other components to depend on configuration without being coupled
// to the конкретной реализации.
//
// This approach:
//
//   - Improves testability (via mocks)
//   - Decouples configuration source from business logic
//   - Provides a stable contract for environment access
//
// # Configuration Model
//
// Environment variables are mapped to the Values struct. Each field:
//
//   - Uses `env` struct tags to define variable names
//   - May define default values via `envDefault`
//   - Supports automatic parsing into Go types (e.g. time.Duration)
//
// Example variables:
//
//	AUTH_SERVER_TIMEOUT
//	    HTTP server request timeout (default: 10s)
//
//	AUTH_SERVER_JWT_HS256_SIGN_KEY
//	    Secret key for signing JWT tokens
//
//	AUTH_SERVER_DEBUG_PPROF_API
//	    Enables pprof debug endpoints (default: false)
//
//	AUTH_SERVER_PGX_POOL_RELOAD_TIMEOUT
//	    Timeout for reloading database connection pool
//
// # Runtime Behavior
//
//   - Environment variables are parsed once at startup
//   - No dynamic reloading is supported
//   - Parsed values are stored in an immutable struct (Values)
//
// # Error Handling
//
// If parsing fails:
//
//   - The error is returned to the caller
//   - The caller is responsible for handling or logging it
//
// Unlike some configuration systems, this package does not silently
// fallback to defaults on parsing errors.
//
// # Thread Safety
//
// The Config struct is effectively immutable after initialization,
// making it safe for concurrent use without additional synchronization.
//
// # Constants
//
// PgxPoolReloadTimeout
//
//	Default fallback duration used for database pool reload operations
//	when not explicitly overridden.
//
// # Interfaces
//
// Environments
//
//	Provides access to environment configuration via:
//
//	    Values() Values
//
//	This abstraction enables mocking and decouples consumers from
//	the concrete Config implementation.
//
// # Functions
//
// EnvironmentsLoad() (*Config, error)
//
//	Parses environment variables into a Config structure and returns it.
//
//	Returns an error if parsing fails.
//
// # Example Usage
//
//	cfg, err := env.EnvironmentsLoad()
//	if err != nil {
//	    log.Fatal(err)
//	}
//
//	values := cfg.Values()
//	fmt.Println(values.Timeout)
package env

import (
	"time"

	"github.com/caarlos0/env/v11"
)

//go:generate mockgen -destination=env_mock_test.go -package=env github.com/vskurikhin/DayBook3/auth/v2/internal/server/env Environments
type Environments interface {
	Values() Values
}

var _ Environments = (*Config)(nil)

type Config struct {
	values Values
}

// Values returns the environment configuration values stored in Config.
func (c Config) Values() Values {
	return c.values
}

const PgxPoolReloadTimeout = 500 * time.Millisecond

// Values статичная конфигурация из переменных окружения.
type Values struct {
	AdvisoryLockSleepDuration time.Duration `env:"AUTH_SERVER_ADVISORY_LOCK_SLEEP_DURATION"`
	DebugPprof                bool          `env:"AUTH_SERVER_DEBUG_PPROF_API" envDefault:"false"`
	JWThs256SignKey           string        `env:"AUTH_SERVER_JWT_HS256_SIGN_KEY" envDefault:""`
	OldPgxPoolCloseTimeout    time.Duration `env:"AUTH_SERVER_OLD_PGX_POOL_CLOSE_TIMEOUT" envDefault:"1m"`
	PgxPoolReloadTimeout      time.Duration `env:"AUTH_SERVER_PGX_POOL_RELOAD_TIMEOUT" envDefault:"500ms"`
	Timeout                   time.Duration `env:"AUTH_SERVER_TIMEOUT" envDefault:"10s"`
	SchedulerJobSleepDuration time.Duration `env:"AUTH_SERVER_SCHEDULER_JOB_SLEEP_DURATION"`
}

// EnvironmentsLoad parses environment variables into a Config structure
// and returns it as an Environments implementation. If parsing fails,
// the error is logged and an empty configuration is returned.
func EnvironmentsLoad() (*Config, error) {
	values := Values{}

	if err := env.Parse(&values); err != nil {
		return nil, err
	}
	return &Config{values: values}, nil
}
