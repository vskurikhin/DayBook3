package services

import (
	"errors"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
)

type BaseService struct {
	cfg config.Config
}

var (
	ErrInvalidUserID = errors.New(xerror.InvalidUserID)
)
