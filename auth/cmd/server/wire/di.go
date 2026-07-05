//go:build wireinject
// +build wireinject

package wire

import (
	"github.com/google/wire"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/actions"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/db"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/env"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/handler"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_attrs"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_has_roles"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_name"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_view"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/creds"
)

var (
	serverSet = wire.NewSet(
		config.GetConfig,
		env.EnvironmentsLoad,
		server.NewAuthServer,
	)
	handlerSet = wire.NewSet(
		handler.NewApiV1,
		handler.NewApiV2,
		handler.NewRouter,
	)
	resourceSet = wire.NewSet(
		wire.Bind(new(resources.ResourceV1), new(*resources.V1)),
		wire.Bind(new(resources.ResourceV2), new(*resources.V2)),
		resources.NewV1,
		resources.NewV2,
	)
	serviceSet = wire.NewSet(
		wire.Bind(new(actions.TxDelayer), new(*actions.TransactionDelayer)),
		wire.Bind(new(creds.CredentialsFactoryV2), new(*creds.CredentialsMethodFactoryV2)),
		wire.Bind(new(services.AuthServiceV2), new(*services.AuthServiceImplV2)),
		wire.Bind(new(services.ListServiceV2), new(*services.ListServiceImplV2)),
		wire.Bind(new(services.LogoutServiceV2), new(*services.LogoutServiceImplV2)),
		wire.Bind(new(services.OkServiceV1), new(*services.OkServiceImplV1)),
		wire.Bind(new(services.OkServiceV2), new(*services.OkServiceImplV2)),
		wire.Bind(new(services.RefreshServiceV2), new(*services.RefreshServiceImplV2)),
		wire.Bind(new(services.RegisterServiceV2), new(*services.RegisterServiceImplV2)),
		wire.Bind(new(services.SessionRolesV2), new(*services.SessionRolesImplV2)),
		actions.NewTransactionDelayer,
		creds.NewCredentialsMethodFactory,
		services.NewAuthServiceV2,
		services.NewListServiceV2,
		services.NewLogoutServiceV2,
		services.NewOkServiceV1,
		services.NewOkServiceV2,
		services.NewRefreshServiceV2,
		services.NewRegisterServiceV2,
		services.NewSessionRolesV2,
	)
	repositorySet = wire.NewSet(
		wire.Bind(new(db.DB), new(*db.PgxPool)),
		wire.Bind(new(session.Repo), new(*session.Queries)),
		wire.Bind(new(session.DBTX), new(*db.PgxPool)),
		wire.Bind(new(user_attrs.Repo), new(*user_attrs.Queries)),
		wire.Bind(new(user_attrs.DBTX), new(*db.PgxPool)),
		wire.Bind(new(user_has_roles.Repo), new(*user_has_roles.Queries)),
		wire.Bind(new(user_has_roles.DBTX), new(*db.PgxPool)),
		wire.Bind(new(user_name.Repo), new(*user_name.Queries)),
		wire.Bind(new(user_name.DBTX), new(*db.PgxPool)),
		wire.Bind(new(user_view.Repo), new(*user_view.Queries)),
		wire.Bind(new(user_view.DBTX), new(*db.PgxPool)),
		db.GetDB,
		session.New,
		user_attrs.New,
		user_has_roles.New,
		user_name.New,
		user_view.New,
	)
)

func InitializeServer(cfg config.Config, environments env.Environments) (*server.AuthServer, error) {
	wire.Build(serverSet, handlerSet, resourceSet, serviceSet, repositorySet)
	return &server.AuthServer{}, nil
}
