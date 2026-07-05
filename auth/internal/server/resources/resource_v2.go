package resources

import (
	"encoding/json"
	"fmt"
	"log/slog"
	"net/http"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/dto"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

type ResourceV2 interface {
	Auth(w http.ResponseWriter, r *http.Request) error
	List(w http.ResponseWriter, r *http.Request) error
	Logout(w http.ResponseWriter, r *http.Request) error
	Ok(w http.ResponseWriter, r *http.Request) error
	Refresh(w http.ResponseWriter, r *http.Request) error
	Register(w http.ResponseWriter, r *http.Request) error
	SessionRoles(w http.ResponseWriter, r *http.Request) error
}

var _ ResourceV2 = (*V2)(nil)

type V2 struct {
	authService         services.AuthServiceV2
	cfg                 config.Config
	listService         services.ListServiceV2
	logoutService       services.LogoutServiceV2
	okService           services.OkServiceV2
	refreshService      services.RefreshServiceV2
	registerService     services.RegisterServiceV2
	sessionRolesService services.SessionRolesV2
}

// Auth godoc
// @Summary Authenticate user
// @Description Authenticates user using login and password. Returns access token and sets refresh token cookie.
// @Tags auth
// @Accept json
// @Produce json
// @Param request body dto.Login true "User credentials"
// @Success 200 {object} APIResponse{data=dto.Token}
// @Failure 400 {object} APIResponse{error=string,success=bool} "bad request"
// @Failure 500 {object} APIResponse{error=string,success=bool} "server error 'success': false"
// @Failure 503 {object} APIResponse{error=string,success=bool} "service unavailable"
// @Failure 504 {object} APIResponse{error=string,success=bool} "gateway timeout"
// @Router /v2/auth [post]
func (v V2) Auth(w http.ResponseWriter, r *http.Request) error {
	body := http.MaxBytesReader(w, r.Body, int64(v.cfg.Values().RequestMaxBytes))
	decoder := json.NewDecoder(body)
	var login dto.Login
	errDecode := decoder.Decode(&login)
	if errDecode != nil {
		return errDecode
	}
	creds, err := v.authService.Auth(r.Context(), model.LoginFromDto(login))
	if err != nil {
		return err
	}
	cookie := creds.RefreshTokenCookie()
	// Set the cookie in the response writer
	http.SetCookie(w, &cookie)

	return json.NewEncoder(w).Encode(APIResponse{
		Success: true,
		Data:    creds.AccessToken().ToDto(),
	})
}

// List godoc
// @Summary Get users list
// @Description Returns list of users. Requires JWT authentication.
// @Tags users
// @Security BearerAuth
// @Produce json
// @Success 200 {object} APIResponse{data=[]dto.User}
// @Failure 401 {object} string "unauthorized"
// @Failure 500 {object} APIResponse{error=string,success=bool} "server error 'success': false"
// @Failure 503 {object} APIResponse{error=string,success=bool} "service unavailable"
// @Failure 504 {object} APIResponse{error=string,success=bool} "gateway timeout"
// @Router /v2/user/list [get]
func (v V2) List(w http.ResponseWriter, r *http.Request) error {
	list, err := v.listService.List(r.Context())
	if err != nil {
		return err
	}
	return json.NewEncoder(w).Encode(APIResponse{
		Success: true,
		Data:    tool.Map(list, model.User.ToDto),
	})
}

// Logout godoc
// @Summary Logout user
// @Description Invalidates user session. Requires JWT authentication.
// @Tags auth
// @Security BearerAuth
// @Produce json
// @Success 200 {object} APIResponse
// @Failure 401 {object} string "unauthorized"
// @Failure 500 {object} APIResponse{error=string,success=bool} "server error 'success': false"
// @Failure 503 {object} APIResponse{error=string,success=bool} "service unavailable"
// @Failure 504 {object} APIResponse{error=string,success=bool} "gateway timeout"
// @Router /v2/logout [post]
func (v V2) Logout(_ http.ResponseWriter, r *http.Request) error {
	return v.logoutService.Logout(r.Context())
}

// Ok godoc
// @Summary Health check
// @Description Returns service status.
// @Tags health
// @Produce json
// @Success 200 {object} APIResponse{data=[]map[string]string}
// @Router /v2/ok [get]
func (v V2) Ok(w http.ResponseWriter, _ *http.Request) error {
	return json.NewEncoder(w).Encode(APIResponse{
		Success: true,
		Data: []map[string]string{
			{"msg": v.okService.Ok()},
		},
	})
}

// Refresh godoc
// @Summary Refresh tokens
// @Description Refreshes access token using refresh token from cookie. Sets new refresh cookie.
// @Tags auth
// @Accept json
// @Produce json
// @Param request body dto.Empty false "User credentials by cookie string"
// @Success 200 {object} APIResponse{data=dto.Token}
// @Success 206
// @Failure 400 {object} APIResponse{error=string,success=bool} "bad request"
// @Failure 401 {object} string "unauthorized"
// @Failure 404 {object} APIResponse{error=string,success=bool} "no rows in result set"
// @Failure 500 {object} APIResponse{error=string,success=bool} "server error 'success': false"
// @Failure 503 {object} APIResponse{error=string,success=bool} "service unavailable"
// @Failure 504 {object} APIResponse{error=string,success=bool} "gateway timeout"
// @Router /v2/refresh [post]
func (v V2) Refresh(w http.ResponseWriter, r *http.Request) error {
	cookie, err := r.Cookie("refresh")
	if err != nil {
		slog.ErrorContext(r.Context(),
			"failed to parse cookie",
			slog.String("error", err.Error()),
			slog.String("errorType", fmt.Sprintf("%T", err)),
		)
		return xerror.ClassingHTTPError(err)
	}
	body := http.MaxBytesReader(w, r.Body, int64(v.cfg.Values().RequestMaxBytes))
	decoder := json.NewDecoder(body)
	var login dto.Login
	errDecode := decoder.Decode(&login)
	if errDecode != nil {
		return errDecode
	}
	_ = login
	creds, err := v.refreshService.Refresh(r.Context(), cookie.Value)
	if err != nil {
		slog.ErrorContext(r.Context(),
			"failed refresh",
			slog.String("error", err.Error()),
			slog.String("errorType", fmt.Sprintf("%T", err)),
		)
		return err
	}
	refreshCookie := creds.RefreshTokenCookie()
	// Set the cookie in the response writer
	http.SetCookie(w, &refreshCookie)
	return json.NewEncoder(w).Encode(APIResponse{
		Success: true,
		Data:    creds.AccessToken().ToDto(),
	})
}

// Register godoc
// @Summary Register user
// @Description Registers a new user and returns access token with refresh cookie.
// @Tags auth
// @Accept json
// @Produce json
// @Param request body dto.CreateUser true "User registration data"
// @Success 200 {object} APIResponse{data=dto.Token}
// @Failure 400 {object} APIResponse{error=string,success=bool} "bad request"
// @Failure 409 {object} APIResponse{error=string,success=bool} "status conflict"
// @Failure 500 {object} APIResponse{error=string,success=bool} "server error 'success': false"
// @Failure 503 {object} APIResponse{error=string,success=bool} "service unavailable"
// @Failure 504 {object} APIResponse{error=string,success=bool} "gateway timeout"
// @Router /v2/register [post]
func (v V2) Register(w http.ResponseWriter, r *http.Request) error {
	body := http.MaxBytesReader(w, r.Body, int64(v.cfg.Values().RequestMaxBytes))
	decoder := json.NewDecoder(body)
	var u dto.CreateUser
	errDecode := decoder.Decode(&u)
	if errDecode != nil {
		return errDecode
	}
	creds, err := v.registerService.Register(r.Context(), model.CreateUserFromDto(u))
	if err != nil {
		return err
	}
	cookie := creds.RefreshTokenCookie()
	// Set the cookie in the response writer
	http.SetCookie(w, &cookie)
	return json.NewEncoder(w).Encode(APIResponse{
		Success: true,
		Data:    creds.AccessToken().ToDto(),
	})
}

// SessionRoles godoc
// @Summary for user get roles in session
// @Description Invalidates user session. Requires JWT authentication.
// @Tags session
// @Security BearerAuth
// @Produce json
// @Success 200 {object} APIResponse{data=dto.UserHasRoles}
// @Failure 401 {object} string "unauthorized"
// @Failure 500 {object} APIResponse{error=string,success=bool} "server error 'success': false"
// @Failure 503 {object} APIResponse{error=string,success=bool} "service unavailable"
// @Failure 504 {object} APIResponse{error=string,success=bool} "gateway timeout"
// @Router /v2/session/roles [get]
func (v V2) SessionRoles(w http.ResponseWriter, r *http.Request) error {
	userHasRoles, err := v.sessionRolesService.SessionRoles(r.Context())
	if err != nil {
		return err
	}
	return json.NewEncoder(w).Encode(APIResponse{
		Success: true,
		Data:    userHasRoles.ToDto(),
	})
}

// NewV2 creates and returns a new V2 resource instance that implements
// the ResourceV2 interface.
func NewV2(
	authService services.AuthServiceV2,
	cfg config.Config,
	listService services.ListServiceV2,
	logoutService services.LogoutServiceV2,
	okService services.OkServiceV2,
	refreshService services.RefreshServiceV2,
	registerService services.RegisterServiceV2,
	sessionRolesService services.SessionRolesV2,
) *V2 {
	return &V2{
		authService:         authService,
		cfg:                 cfg,
		listService:         listService,
		logoutService:       logoutService,
		okService:           okService,
		refreshService:      refreshService,
		registerService:     registerService,
		sessionRolesService: sessionRolesService,
	}
}
