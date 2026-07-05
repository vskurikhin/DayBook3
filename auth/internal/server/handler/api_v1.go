package handler

import (
	"github.com/go-chi/chi/v5"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/resources"
)

type ApiV1 interface {
	chi.Router
}

// NewApiV1 creates and configures a chi router for version 1 of the API.
// It registers HTTP routes that delegate request handling to the provided
// ResourceV1 implementation.
func NewApiV1(v1 resources.ResourceV1) ApiV1 {
	r := chi.NewRouter()
	r.Get(OkURL, v1.Ok)
	return r
}
