package resources

import (
	"context"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
)

//go:generate mockgen -destination=mock_auth_service_v2_test.go -package=resources github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources AuthServiceV2
type AuthServiceV2 interface {
	Auth(ctx context.Context, login model.Login) (model.Credentials, error)
}

//go:generate mockgen -destination=mock_config_test.go -package=resources github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources Config
type Config interface {
	JWThs256SignKey(string)
	Values() config.Values
}

//go:generate mockgen -destination=mock_list_service_v2_test.go -package=resources github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources ListServiceV2
type ListServiceV2 interface {
	List(ctx context.Context) ([]model.User, error)
}

//go:generate mockgen -destination=mock_logout_service_v2_test.go -package=resources github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources LogoutServiceV2
type LogoutServiceV2 interface {
	Logout(ctx context.Context) error
}

//go:generate mockgen -destination=mock_ok_service_v1_test.go -package=resources github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources OkServiceV1
type OkServiceV1 interface {
	Ok() string
}

//go:generate mockgen -destination=mock_ok_service_v2_test.go -package=resources github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources OkServiceV2
type OkServiceV2 interface {
	Ok() string
}

//go:generate mockgen -destination=mock_refresh_service_v2_test.go -package=resources github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources RefreshServiceV2
type RefreshServiceV2 interface {
	Refresh(ctx context.Context, token string) (model.Credentials, error)
}

//go:generate mockgen -destination=mock_register_service_v2_test.go -package=resources github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources RegisterServiceV2
type RegisterServiceV2 interface {
	Register(ctx context.Context, user model.CreateUser) (model.Credentials, error)
}

//go:generate mockgen -destination=mock_session_roles_v2_test.go -package=resources github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources SessionRolesV2
type SessionRolesV2 interface {
	SessionRoles(ctx context.Context) (model.UserHasRoles, error)
}
