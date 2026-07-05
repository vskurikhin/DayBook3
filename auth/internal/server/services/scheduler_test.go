package services

import (
	"context"
	"errors"
	"sync"
	"testing"
	"time"

	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/stretchr/testify/require"
	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/db"
)

func TestScheduler_job(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	type fields struct {
		setupDB func(m *MockDB, conn *MockPgxConn, row *MockRow)
	}

	tests := []struct {
		name    string
		fields  fields
		wantErr bool
	}{
		{
			name: "Acquire error",
			fields: fields{
				setupDB: func(m *MockDB, conn *MockPgxConn, row *MockRow) {
					m.EXPECT().
						Acquire(gomock.Any()).
						Return(nil, errors.New("db error"))
				},
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mockDB := NewMockDB(ctrl)
			mockPgxConn := NewMockPgxConn(ctrl)
			mockRow := NewMockRow(ctrl)
			tt.fields.setupDB(mockDB, mockPgxConn, mockRow)

			ctx, cancel := context.WithCancel(context.Background())

			s := &scheduler{
				BaseService: &BaseService{},
				db:          mockDB,
				fs:          []func(config.Config, db.DB) error{(&testJob{}).job},
			}

			// 🔴 прерываем через время (иначе бесконечный цикл)
			go func() {
				time.Sleep(50 * time.Millisecond)
				cancel()
			}()

			err := s.job(ctx)

			if tt.wantErr {
				require.Error(t, err)
			}
		})
	}
}

func TestScheduler_callFunctions(t *testing.T) {
	tests := []struct {
		name string
		fs   []func(config.Config, db.DB) error
	}{
		{
			name: "all success",
			fs: []func(config.Config, db.DB) error{
				func(config.Config, db.DB) error { return nil },
			},
		},
		{
			name: "one fails",
			fs: []func(config.Config, db.DB) error{
				func(config.Config, db.DB) error { return errors.New("fail") },
			},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			s := &scheduler{
				BaseService: &BaseService{},
				fs:          tt.fs,
			}

			// не должен паниковать
			s.callFunctions(context.Background())
		})
	}
}

func TestScheduler_connectionJob_LockFail(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockPgxConn := NewMockPgxConn(ctrl)
	mockRow := NewMockRow(ctrl)

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// 🔴 прерываем через время (иначе бесконечный цикл)
	go func() {
		time.Sleep(500*time.Millisecond + 100*time.Millisecond)
		cancel()
	}()

	mockRow.EXPECT().
		Scan(gomock.Any()).
		DoAndReturn(func(dest ...any) error {
			*(dest[0].(*bool)) = true
			return nil
		})
	mockPgxConn.EXPECT().
		Exec(ctx, gomock.Any(), gomock.Any()).
		Return(pgconn.CommandTag{}, nil)
	mockPgxConn.EXPECT().
		QueryRow(ctx, gomock.Any(), gomock.Any()).
		//QueryRow(ctx, "SELECT pg_try_advisory_lock($1)", KeyLock).
		Return(mockRow)

	s := &scheduler{
		advisoryLockDuration: 500 * time.Millisecond,
		jobSleepDuration:     50 * time.Millisecond,
	}

	err := s.connectionJob(ctx, mockPgxConn)
	require.Error(t, err)
}

func TestScheduler_connectionJob_LockFalse(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockPgxConn := NewMockPgxConn(ctrl)
	mockRow := NewMockRow(ctrl)

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// 🔴 прерываем через время (иначе бесконечный цикл)
	go func() {
		time.Sleep(500*time.Millisecond + 100*time.Millisecond)
		cancel()
	}()

	mockRow.EXPECT().
		Scan(gomock.Any()).
		DoAndReturn(func(dest ...any) error {
			*(dest[0].(*bool)) = false
			return nil
		})
	mockPgxConn.EXPECT().
		Exec(ctx, gomock.Any(), gomock.Any()).
		Return(pgconn.CommandTag{}, nil).AnyTimes()
	mockPgxConn.EXPECT().
		QueryRow(ctx, gomock.Any(), gomock.Any()).
		Return(mockRow).AnyTimes()

	s := &scheduler{
		advisoryLockDuration: 500 * time.Millisecond,
		jobSleepDuration:     50 * time.Millisecond,
	}

	err := s.connectionJob(ctx, mockPgxConn)
	require.NoError(t, err)
}

func TestScheduler_RunScheduler(t *testing.T) {
	oldSchedulerOnce := schedulerOnce
	defer func() { schedulerOnce = oldSchedulerOnce }()
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()
	schedulerOnce = new(sync.Once)

	mockCfg := NewMockConfig(ctrl)
	mockDB := NewMockDB(ctrl)

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// 🔴 прерываем через время (иначе бесконечный цикл)
	go func() {
		time.Sleep(50 * time.Millisecond)
		cancel()
	}()

	mockCfg.EXPECT().
		Values().
		Return(config.Values{
			AdvisoryLockSleepDuration: 100 * time.Millisecond,
			SchedulerJobSleepDuration: 100 * time.Millisecond,
		}).AnyTimes()
	mockDB.EXPECT().
		Acquire(gomock.Any()).
		Return(&pgxpool.Conn{}, nil).
		AnyTimes()

	var job = func(config.Config, db.DB) error { return nil }

	err := RunScheduler(ctx, mockCfg, mockDB, job)
	require.NoError(t, err)
}

type testJob struct {
}

func (s *testJob) job(config.Config, db.DB) error {
	return nil
}
