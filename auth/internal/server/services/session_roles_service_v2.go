package services

import (
	"context"
	"fmt"
	"log/slog"

	"github.com/go-chi/jwtauth/v5"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
)

type SessionRolesV2 interface {
	SessionRoles(ctx context.Context) (model.UserHasRoles, error)
}

var _ SessionRolesV2 = (*SessionRolesImplV2)(nil)

type SessionRolesImplV2 struct {
	*BaseService
	sessionRepo session.Repo
}

// SessionRoles извлекает роли пользователя из текущей сессии,
// получая SessionID из JWT токена и запрашивая данные из репозитория.
func (s *SessionRolesImplV2) SessionRoles(ctx context.Context) (model.UserHasRoles, error) {
	bearerToken, _, errBearerToken := jwtauth.FromContext(ctx)
	if errBearerToken != nil {
		slog.ErrorContext(ctx,
			"failed to parse bearer",
			slog.String("error", errBearerToken.Error()),
			slog.String("errorType", fmt.Sprintf("%T", errBearerToken)),
		)
		return model.UserHasRoles{}, errBearerToken
	}

	sid, errDecodeSessionID := model.SessionIDFromJwxToken(bearerToken)
	if errDecodeSessionID != nil {
		slog.ErrorContext(ctx,
			"failed to decode SessionID",
			slog.String("error", errDecodeSessionID.Error()),
			slog.String("errorType", fmt.Sprintf("%T", errDecodeSessionID)),
		)
		return model.UserHasRoles{}, errDecodeSessionID
	}
	primaryKey := sid.ToModelPrimaryKey()

	sess, errGetSession := s.sessionRepo.GetSession(ctx, session.GetSessionParams{
		Iss: primaryKey.Iss,
		Jti: primaryKey.Jti,
		Sub: primaryKey.Sub,
	})
	if errGetSession != nil {
		slog.ErrorContext(ctx,
			"failed to decode SessionID",
			slog.String("error", errGetSession.Error()),
			slog.String("errorType", fmt.Sprintf("%T", errGetSession)),
		)
		return model.UserHasRoles{}, errGetSession
	}
	return model.UserHasRolesFromModelSession(sess), nil
}

// NewSessionRolesV2 создаёт новый сервис для получения ролей пользователя по сессии.
func NewSessionRolesV2(cfg config.Config, sessionRepo session.Repo) *SessionRolesImplV2 {
	return &SessionRolesImplV2{
		BaseService: &BaseService{
			cfg: cfg,
		},
		sessionRepo: sessionRepo,
	}
}
