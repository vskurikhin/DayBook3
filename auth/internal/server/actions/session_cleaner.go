package actions

import (
	"context"
	"log/slog"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/db"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
)

type SessionCleaner struct {
}

func (s *SessionCleaner) Clean(_ config.Config, db db.DB) error {
	sessionRepo := session.New(db)
	err := sessionRepo.DeleteSessionWhereValidTimeLessThanNow(context.Background())
	if err != nil {
		slog.Error("failed to delete session cleaner", slog.String("error", err.Error()))
		return nil
	}
	slog.Info("session cleaner deleted")
	return nil
}
