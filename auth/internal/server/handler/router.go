// Package handler provides HTTP routing and middleware configuration
// for the auth server.
//
// The package defines the HTTP layer of the application using the chi router.
// It is responsible for:
//
//   - Creating and configuring the HTTP router
//   - Registering global middleware (CORS, logging, recovery, timeout, etc.)
//   - Defining API endpoints
//
// The router is created via the NewRouter function, which accepts a
// config.Config instance. Configuration values (such as Debug mode)
// influence middleware behavior (for example, CORS debug logging).
//
// Middleware stack includes:
//
//   - CORS support (github.com/rs/cors)
//   - Request ID injection
//   - Real IP detection
//   - Structured logging
//   - Panic recovery
//   - Request timeout handling
package handler

import (
	"net/http"
	"net/http/pprof"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/rs/cors"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/env"
)

//goland:noinspection SpellCheckingInspection
const (
	Allocs           = "allocs"
	AllocsURL        = "/" + Allocs
	AuthURL          = "/auth"
	BaseURL          = "/auth/api"
	Block            = "block"
	BlockURL         = "/" + Block
	CmdLineURL       = "/cmdline"
	DebugURL         = "/debug"
	GoRoutine        = "goroutine"
	GoRoutineLeak    = "goroutineleak"
	GoRoutineLeakURL = "/" + GoRoutineLeak
	GoRoutineURL     = "/" + GoRoutine
	Heap             = "heap"
	HeapURL          = "/" + Heap
	LogoutURL        = "/logout"
	Mutex            = "mutex"
	MutexURL         = "/" + Mutex
	OkURL            = "/ok"
	ProfileURL       = "/profile"
	RefreshURL       = "/refresh"
	RegisterURL      = "/register"
	SessionRolesURL  = "/session/roles"
	SymbolURL        = "/symbol"
	ThreadCreate     = "threadcreate"
	ThreadCreateURL  = "/" + ThreadCreate
	Trace            = "/trace"
	UserListURL      = "/user/list"
	V1               = "/v1"
	V2               = "/v2"
)

//go:generate mockgen -destination=z_mock_config_test.go -package=handler github.com/vskurikhin/DayBook3/auth/v2/internal/server/handler Config
type Config interface {
	JWThs256SignKey(string)
	Values() config.Values
}

// NewRouter creates and configures an HTTP router with
// middleware and application routes using the provided configuration.
func NewRouter(cfg config.Config, env env.Environments, v1 ApiV1, v2 ApiV2) http.Handler {
	r := chi.NewRouter()

	c := cors.New(cors.Options{
		AllowedOrigins: []string{"*"},                                       // All origins
		AllowedMethods: []string{"POST", "GET", "PUT", "DELETE", "OPTIONS"}, // Allowing only get, just an example
		Debug:          cfg.Values().Debug,
	})

	r.Use(c.Handler)
	r.Use(middleware.RequestID)
	r.Use(middleware.RealIP)
	r.Use(middleware.Logger)
	r.Use(middleware.Recoverer)

	// Set a timeout value on the request context (ctx), that will signal
	// through ctx.Done() that the request has timed out and further
	// processing should be stopped.
	r.Use(middleware.Timeout(env.Values().Timeout))

	r.Mount(BaseURL+V1, v1)
	r.Mount(BaseURL+V2, v2)

	if env.Values().DebugPprof {
		// Or mount the entire pprof handler set
		r.Route(DebugURL, func(r chi.Router) {
			r.HandleFunc("/", pprof.Index)
			r.HandleFunc(CmdLineURL, pprof.Cmdline)
			r.HandleFunc(ProfileURL, pprof.Profile)
			r.HandleFunc(SymbolURL, pprof.Symbol)
			r.HandleFunc(Trace, pprof.Trace)
			r.Handle(AllocsURL, pprof.Handler(Allocs))
			r.Handle(BlockURL, pprof.Handler(Block))
			r.Handle(GoRoutineLeakURL, pprof.Handler(GoRoutineLeak))
			r.Handle(GoRoutineURL, pprof.Handler(GoRoutine))
			r.Handle(HeapURL, pprof.Handler(Heap))
			r.Handle(MutexURL, pprof.Handler(Mutex))
			r.Handle(ThreadCreateURL, pprof.Handler(ThreadCreate))
		})
	}
	return r
}
