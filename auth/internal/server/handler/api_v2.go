package handler

import (
	"net/http"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/jwtauth/v5"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources"
)

type ApiV2 interface {
	chi.Router
}

// NewApiV2 creates and configures a chi router for version 2 of the API.
// It registers HTTP routes that delegate request handling to the provided
// ResourceV2 implementation.
func NewApiV2(cfg config.Config, v2 resources.ResourceV2) ApiV2 {
	r := chi.NewRouter()
	r.Use(middleware.AllowContentType("application/json"))
	r.Method(http.MethodPost, RegisterURL, APISyncHandler(v2.Register))
	r.Method(http.MethodPost, RefreshURL, APISyncHandler(v2.Refresh))
	r.Method(http.MethodPost, AuthURL, APISyncHandler(v2.Auth))
	r.Method(http.MethodGet, OkURL, APIHandler(v2.Ok))
	// Protected routes
	r.Group(func(r chi.Router) {
		tokenAuth := jwtauth.New("HS256", cfg.Values().JWThs256SignKey, nil)
		// Seek, verify and validate JWT tokens
		r.Use(jwtauth.Verifier(tokenAuth))
		r.Use(jwtauth.Authenticator(tokenAuth))
		r.Method(http.MethodGet, UserListURL, APISyncHandler(v2.List))
		r.Method(http.MethodPost, LogoutURL, APISyncHandler(v2.Logout))
		r.Method(http.MethodGet, SessionRolesURL, APISyncHandler(v2.SessionRoles))
	})

	return r
}
