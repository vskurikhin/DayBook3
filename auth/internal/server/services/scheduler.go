package services

import (
	"context"
	"fmt"
	"log/slog"
	"sync"
	"time"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/db"
	al "github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/advisory_lock"
)

const KeyLock = 13041976

type scheduler struct {
	*BaseService
	advisoryLockDuration time.Duration
	db                   db.DB
	fs                   []func(config.Config, db.DB) error
	jobSleepDuration     time.Duration
}

var schedulerOnce = new(sync.Once)

// RunScheduler initializes and starts a singleton scheduler that executes
// the provided functions periodically under a distributed advisory lock.
//
// The scheduler ensures that only one instance is running across multiple
// processes by using a database-level advisory lock. It acquires a database
// connection and periodically attempts to obtain the lock. If the lock is
// successfully acquired, the scheduler starts executing the given functions
// (fs) in a loop with a configured sleep interval between executions.
//
// The scheduler runs until the provided context is canceled or an error occurs.
// If the context is canceled, the function returns the context error.
//
// The scheduler is initialized only once using sync.Once. Subsequent calls
// to RunScheduler will have no effect.
//
// Parameters:
//   - ctx: context used for cancellation and timeout control.
//   - cfg: application configuration containing scheduler timing settings.
//   - db: database instance used for connection management and advisory locking.
//   - fs: variadic list of functions to be executed periodically. Each function
//     receives the configuration and database instance and returns an error.
//
// Returns:
//   - error: any error encountered during scheduler execution or context cancellation.
func RunScheduler(ctx context.Context, cfg config.Config, db db.DB, fs ...func(config.Config, db.DB) error) error {
	var (
		err error
		s   scheduler
	)
	schedulerOnce.Do(func() {
		values := cfg.Values()
		s = scheduler{
			BaseService:          &BaseService{cfg: cfg},
			advisoryLockDuration: values.AdvisoryLockSleepDuration,
			db:                   db,
			fs:                   fs,
			jobSleepDuration:     values.SchedulerJobSleepDuration,
		}
		go func() {
			err = s.job(ctx)
		}()
	})
	return err
}

func (s *scheduler) job(ctx context.Context) error {
	conn, errAcquire := s.db.Acquire(ctx)
	if errAcquire != nil {
		return errAcquire
	}
	defer conn.Release()
	for {
		select {
		case <-ctx.Done():
			return ctx.Err()
		case <-time.After(s.advisoryLockDuration):
			errJob := s.connectionJob(ctx, conn)
			if errJob != nil {
				return errJob
			}
		}
	}
}

func (s *scheduler) connectionJob(ctx context.Context, conn db.PgxConn) error {
	lockOk, errAcquireLock := al.AcquireLock(ctx, conn, KeyLock)
	if errAcquireLock != nil {
		return errAcquireLock
	}
	if !lockOk {
		return nil
	}
	defer func() { _ = al.ReleaseLock(ctx, conn, KeyLock) }()
	for {
		select {
		case <-ctx.Done():
			return ctx.Err()
		case <-time.After(s.jobSleepDuration):
			s.callFunctions(ctx)
		}
	}
}

func (s *scheduler) callFunctions(ctx context.Context) {
	for _, f := range s.fs {
		err := f(s.cfg, s.db)
		if err != nil {
			slog.ErrorContext(ctx,
				"Scheduler",
				slog.String("error", err.Error()),
				slog.String("errorType", fmt.Sprintf("%T", err)),
			)
		}
	}
}
