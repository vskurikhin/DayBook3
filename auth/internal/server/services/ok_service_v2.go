package services

import (
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/db"
)

const Ok = "Ok"

type OkServiceV2 OkServiceV1

var _ OkServiceV1 = (*OkServiceImplV2)(nil)
var _ OkServiceV2 = (*OkServiceImplV2)(nil)

type OkServiceImplV2 struct {
	*OkServiceImplV1
}

func (s *OkServiceImplV2) Ok() string {
	return Ok
}

func NewOkServiceV2(cfg config.Config, dbPool db.DB) *OkServiceImplV2 {
	return &OkServiceImplV2{
		OkServiceImplV1: NewOkServiceV1(cfg, dbPool),
	}
}
