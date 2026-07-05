package services

import (
	"context"
	"fmt"
	"log/slog"
	"time"

	"github.com/golang-jwt/jwt/v5"
	"github.com/jackc/pgx/v5/pgtype"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_view"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/creds"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

type RefreshServiceV2 interface {
	Refresh(ctx context.Context, token string) (model.Credentials, error)
}

var _ RefreshServiceV2 = (*RefreshServiceImplV2)(nil)

type RefreshServiceImplV2 struct {
	*BaseService
	credentialsFactory creds.CredentialsFactoryV2
	sessionRepo        session.Repo
	userViewRepo       user_view.Repo
}

// Refresh validates the provided JWT token and issues new credentials.
func (s *RefreshServiceImplV2) Refresh(ctx context.Context, token string) (model.Credentials, error) {
	parsedToken, err := jwt.ParseWithClaims(token, &tool.CustomClaims{}, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(s.cfg.Values().JWThs256SignKey), nil
	})
	if err != nil {
		slog.ErrorContext(ctx,
			"failed to parse token",
			slog.String("error", err.Error()),
			slog.String("errorType", fmt.Sprintf("%T", err)),
		)
		return model.Credentials{}, err
	}
	return s.credentialsFactory.MakeCredentials(s.refresh(ctx, parsedToken.Claims))
}

func (s *RefreshServiceImplV2) refresh(ctx context.Context, claims jwt.Claims) (model.CredValuesV2, error) {
	sid, errDecodeSessionID := model.SessionIDFromClaims(claims)
	if errDecodeSessionID != nil {
		slog.ErrorContext(ctx,
			"failed to decode SessionID",
			slog.String("error", errDecodeSessionID.Error()),
			slog.String("errorType", fmt.Sprintf("%T", errDecodeSessionID)),
		)
		return model.CredValuesV2{}, errDecodeSessionID
	}

	primaryKey := sid.ToModelPrimaryKey()
	sess, errGetSession := s.sessionRepo.GetSession(ctx, session.GetSessionParams{
		Iss: primaryKey.Iss,
		Jti: primaryKey.Jti,
		Sub: primaryKey.Sub,
	})
	if errGetSession != nil {
		return model.CredValuesV2{}, xerror.ClassingPgError(errGetSession)
	}
	if sess.Jti != primaryKey.Jti {
		return model.CredValuesV2{}, xerror.ErrInvalidToken
	}
	if !sess.ValidTime.Valid {
		return model.CredValuesV2{}, xerror.ErrSessionTimeExpired
	}

	cfgValues := s.cfg.Values()
	if time.Now().Add(cfgValues.ValidPeriodAccessToken).After(sess.ValidTime.Time) {
		slog.ErrorContext(ctx,
			"session expired",
			slog.String("error", xerror.ErrSessionTimeExpired.Error()),
			slog.String("errorType", fmt.Sprintf("%T", xerror.ErrSessionTimeExpired)),
		)
		err := s.sessionRepo.DeleteSession(ctx, session.DeleteSessionParams{
			Iss: primaryKey.Iss,
			Jti: primaryKey.Jti,
			Sub: primaryKey.Sub,
		})
		if err != nil {
			slog.ErrorContext(ctx,
				"failed to delete session",
				slog.String("error", err.Error()),
				slog.String("errorType", fmt.Sprintf("%T", err)),
			)
			return model.CredValuesV2{}, xerror.ErrSessionTimeExpired
		}
		return model.CredValuesV2{}, xerror.ErrSessionTimeExpired
	}

	userView, err := s.userViewRepo.GetUserName(ctx, pgtype.Text{String: sess.UserName, Valid: true})
	if err != nil {
		slog.ErrorContext(ctx,
			"failed to get user attributes",
			slog.String("error", err.Error()),
			slog.String("errorType", fmt.Sprintf("%T", err)),
		)
		return model.CredValuesV2{}, err
	}
	validPeriodAccessToken := cfgValues.ValidPeriodAccessToken
	if cfgValues.ValidPeriodAccessToken > sess.ValidTime.Time.Sub(time.Now().Add(-time.Second)) {
		validPeriodAccessToken = sess.ValidTime.Time.Sub(time.Now().Add(-time.Second))
	}
	validTimePeriodsTokens := model.MakeValidTimeTokens(validPeriodAccessToken, sess.ValidTime.Time.Sub(time.Now()))

	user := model.UserFromModelUserView(userView)

	return model.MakeCredValuesV2(sid, validTimePeriodsTokens, user), nil
}

func NewRefreshServiceV2(
	cfg config.Config,
	credentialsFactory creds.CredentialsFactoryV2,
	sessionRepo session.Repo,
	userViewRepo user_view.Repo,
) *RefreshServiceImplV2 {
	return &RefreshServiceImplV2{
		BaseService: &BaseService{
			cfg: cfg,
		},
		credentialsFactory: credentialsFactory,
		sessionRepo:        sessionRepo,
		userViewRepo:       userViewRepo,
	}
}
