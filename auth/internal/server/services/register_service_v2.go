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
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_attrs"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_name"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/creds"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

type RegisterServiceV2 interface {
	Register(ctx context.Context, user model.CreateUser) (model.Credentials, error)
}

var _ RegisterServiceV2 = (*RegisterServiceImplV2)(nil)

type RegisterServiceImplV2 struct {
	*BaseService
	credentialsFactory creds.CredentialsFactoryV2
	dbPool             db.DB
	sessionRepo        session.Repo
	txDelayer          actions.TxDelayer
	userAttrsRepo      user_attrs.Repo
	userNameRepo       user_name.Repo
}

// Register creates a new user, hashes the password, and returns credentials.
func (s *RegisterServiceImplV2) Register(ctx context.Context, user model.CreateUser) (model.Credentials, error) {
	cost := int(s.cfg.Values().AuthCost)
	password, err := tool.Hash(user.Password(), cost)
	if err != nil {
		slog.ErrorContext(ctx,
			"failed to hash password",
			slog.String("error", err.Error()),
			slog.String("errorType", fmt.Sprintf("%T", err)),
		)
		return model.Credentials{}, err
	}
	user.SetHashedPassword(password)
	return s.credentialsFactory.MakeCredentials(s.transactionRegister(ctx, user))
}

func (s *RegisterServiceImplV2) transactionRegister(ctx context.Context, user model.CreateUser) (model.CredValuesV2, error) {
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

	sessionRepoTx := s.sessionRepo.WithTx(tx)
	userAttrsRepoTx := s.userAttrsRepo.WithTx(tx)
	userNameRepoTx := s.userNameRepo.WithTx(tx)
	userName, errTransaction := userNameRepoTx.CreateUserName(ctx, user.ToModelCreateUserNameParams())

	if errTransaction != nil {
		switch xerror.ClassingPgError(errTransaction) {
		case xerror.ErrUniqueViolation:
			errTransaction = xerror.ErrUserExists
		default:
			errTransaction = xerror.ClassingPgError(errTransaction)
		}
		return model.CredValuesV2{}, errTransaction
	}
	if !userName.ID.Valid {
		errTransaction = ErrInvalidUserID
		return model.CredValuesV2{}, errTransaction
	}

	user.SetId(userName.ID)
	_, errTransaction = userAttrsRepoTx.CreateUserAttrs(ctx, user.ToModelCreateUserAttrsParams())
	if errTransaction != nil {
		return model.CredValuesV2{}, xerror.ClassingPgError(errTransaction)
	}

	cfgValues := s.cfg.Values()
	sid, errTransaction := model.MakeSessionID(cfgValues.Hostname, userName.UserName)
	if errTransaction != nil {
		return model.CredValuesV2{}, errTransaction
	}

	validPeriodAccessToken := model.MakeValidTimeTokens(cfgValues.ValidPeriodAccessToken, cfgValues.ValidPeriodRefreshToken)
	primaryKey := sid.ToModelPrimaryKey()
	_, errTransaction = sessionRepoTx.CreateSession(ctx, session.CreateSessionParams{
		Iss:       primaryKey.Iss,
		Jti:       primaryKey.Jti,
		Sub:       primaryKey.Sub,
		UserName:  userName.UserName,
		Roles:     []string{"GUEST"},
		ValidTime: pgtype.Timestamptz{Time: validPeriodAccessToken.SessionValidTime().Local(), Valid: true},
	})
	if errTransaction != nil {
		return model.CredValuesV2{}, xerror.ClassingPgError(errTransaction)
	}
	userResult := model.UserFromModelUserName(userName).
		WithName(user.Name()).
		WithEMail(user.Email())

	return model.MakeCredValuesV2(sid, validPeriodAccessToken, userResult), errTransaction
}

func NewRegisterServiceV2(
	cfg config.Config,
	credentialsFactory creds.CredentialsFactoryV2,
	dbPool db.DB,
	sessionRepo session.Repo,
	txDelayer actions.TxDelayer,
	userAttrsRepo user_attrs.Repo,
	userNameRepo user_name.Repo,
) *RegisterServiceImplV2 {
	return &RegisterServiceImplV2{
		BaseService: &BaseService{
			cfg: cfg,
		},
		credentialsFactory: credentialsFactory,
		dbPool:             dbPool,
		sessionRepo:        sessionRepo,
		txDelayer:          txDelayer,
		userAttrsRepo:      userAttrsRepo,
		userNameRepo:       userNameRepo,
	}
}
