package services

import (
	"context"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_view"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

type ListServiceV2 interface {
	List(ctx context.Context) ([]model.User, error)
}

var _ ListServiceV2 = (*ListServiceImplV2)(nil)

type ListServiceImplV2 struct {
	*BaseService
	userViewRepo user_view.Repo
}

func (s *ListServiceImplV2) List(ctx context.Context) ([]model.User, error) {
	list, err := s.userViewRepo.ListUserNames(ctx)
	if err != nil {
		return nil, err
	}
	return tool.Map(list, model.UserFromModelUserView), err
}

func NewListServiceV2(
	cfg config.Config,
	userViewRepo user_view.Repo,
) *ListServiceImplV2 {
	return &ListServiceImplV2{
		BaseService: &BaseService{
			cfg: cfg,
		},
		userViewRepo: userViewRepo,
	}
}
