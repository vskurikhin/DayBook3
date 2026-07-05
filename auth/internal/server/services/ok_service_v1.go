package services

import (
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/db"
)

const ok = "ok"

type OkServiceV1 interface {
	Ok() string
}

var _ OkServiceV1 = (*OkServiceImplV1)(nil)

type OkServiceImplV1 struct {
	*BaseService
	dbPool db.DB
}

func (s *OkServiceImplV1) Ok() string {
	return ok
}

func NewOkServiceV1(cfg config.Config, dbPool db.DB) *OkServiceImplV1 {
	return &OkServiceImplV1{
		BaseService: &BaseService{
			cfg: cfg,
		},
		dbPool: dbPool,
	}
}
