package services

import (
	"context"
	"fmt"
	"log/slog"

	"github.com/jackc/pgx/v5/pgtype"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/actions"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/db"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_has_roles"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_view"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/creds"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

type AuthServiceV2 interface {
	Auth(ctx context.Context, login model.Login) (model.Credentials, error)
}

var _ AuthServiceV2 = (*AuthServiceImplV2)(nil)

type AuthServiceImplV2 struct {
	*BaseService
	dbPool             db.DB
	credentialsFactory creds.CredentialsFactoryV2
	sessionRepo        session.Repo
	txDelayer          actions.TxDelayer
	userHasRoles       user_has_roles.Repo
	userViewRepo       user_view.Repo
}

// Auth выполняет аутентификацию пользователя и возвращает его учётные данные, а так же
// выполняет проверку пользователя, валидацию пароля и формирует данные для создания сессии.
func (s *AuthServiceImplV2) Auth(ctx context.Context, login model.Login) (model.Credentials, error) {
	return s.credentialsFactory.MakeCredentials(s.auth(ctx, login))
}

func (s *AuthServiceImplV2) auth(ctx context.Context, login model.Login) (model.CredValuesV2, error) {
	userView, err := s.userViewRepo.GetUserName(ctx, login.UserNamePgTypeText())
	if err != nil {
		slog.ErrorContext(ctx,
			"failed to get user name:",
			slog.String("error", err.Error()),
			slog.String("login", login.UserNamePgTypeText().String),
		)
		return model.CredValuesV2{}, err
	}
	if !userView.Password.Valid {
		return model.CredValuesV2{}, xerror.ErrPasswordNotValid
	}
	if !userView.UserName.Valid {
		return model.CredValuesV2{}, xerror.ErrInvalidToken
	}

	if !tool.Verify(userView.Password.String, login.Password()) {
		return model.CredValuesV2{}, xerror.ErrInvalidPassword
	}

	cfgValues := s.cfg.Values()
	sid, errDecodeSessionID := model.MakeSessionID(cfgValues.Hostname, userView.UserName.String)
	if errDecodeSessionID != nil {
		slog.ErrorContext(ctx,
			"failed to decode session id:",
			slog.String("error", errDecodeSessionID.Error()),
			slog.String("errorType", fmt.Sprintf("%T", errDecodeSessionID)),
		)
		return model.CredValuesV2{}, errDecodeSessionID
	}
	return s.transactionAuth(ctx, sid, userView)
}

func (s *AuthServiceImplV2) transactionAuth(
	ctx context.Context, sid model.SessionID, userView user_view.UserView,
) (model.CredValuesV2, error) {
	var errTransaction = xerror.ErrNil
	tx, errTransaction := s.dbPool.Begin(ctx)
	if errTransaction != nil {
		slog.ErrorContext(ctx,
			"failed to begin transaction",
			slog.String("error", errTransaction.Error()),
			slog.String("errorType", fmt.Sprintf("%T", errTransaction)),
		)
		return model.CredValuesV2{}, errTransaction
	}
	defer func() { s.txDelayer.Defer(ctx, tx, errTransaction) }()

	userHasRolesTx := s.userHasRoles.WithTx(tx)
	userHasRoles, errRolesForUser := userHasRolesTx.GetRolesForUserName(ctx, userView.UserName.String)
	if errRolesForUser != nil {
		slog.ErrorContext(ctx,
			"failed to get roles for user",
			slog.String("error", errRolesForUser.Error()),
			slog.String("errorType", fmt.Sprintf("%T", errTransaction)),
		)
		return model.CredValuesV2{}, errRolesForUser
	}
	slog.ErrorContext(ctx,
		"get roles for user",
		slog.String("Roles", fmt.Sprintf("%+v", userHasRoles.Roles)),
	)
	sessionRepoTx := s.sessionRepo.WithTx(tx)
	cfgValues := s.cfg.Values()
	validTimePeriodsTokens := model.MakeValidTimeTokens(cfgValues.ValidPeriodAccessToken, cfgValues.ValidPeriodRefreshToken)
	primaryKey := sid.ToModelPrimaryKey()
	_, errTransaction = sessionRepoTx.CreateSession(ctx, session.CreateSessionParams{
		Iss:       primaryKey.Iss,
		Jti:       primaryKey.Jti,
		Sub:       primaryKey.Sub,
		UserName:  userView.UserName.String,
		Roles:     userHasRoles.Roles,
		ValidTime: pgtype.Timestamptz{Time: validTimePeriodsTokens.SessionValidTime().Local(), Valid: true},
	})
	if errTransaction != nil {
		return model.CredValuesV2{}, xerror.ClassingPgError(errTransaction)
	}
	user := model.UserFromModelUserView(userView)

	return model.MakeCredValuesV2(sid, validTimePeriodsTokens, user), errTransaction
}

// NewAuthServiceV2 создаёт новый сервис аутентификации с необходимыми зависимостями.
func NewAuthServiceV2(
	cfg config.Config,
	credentialsFactory creds.CredentialsFactoryV2,
	dbPool db.DB,
	sessionRepo session.Repo,
	txDelayer actions.TxDelayer,
	userHasRoles user_has_roles.Repo,
	userViewRepo user_view.Repo,
) *AuthServiceImplV2 {
	return &AuthServiceImplV2{
		BaseService: &BaseService{
			cfg: cfg,
		},
		credentialsFactory: credentialsFactory,
		dbPool:             dbPool,
		txDelayer:          txDelayer,
		sessionRepo:        sessionRepo,
		userHasRoles:       userHasRoles,
		userViewRepo:       userViewRepo,
	}
}
