package services

import (
	"context"
	"testing"

	"github.com/go-chi/jwtauth/v5"
	"github.com/stretchr/testify/assert"
	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
)

func TestLogoutServiceImplV2_Logout(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockCfg := NewMockConfig(ctrl)
	mockSessionRepo := NewMockSessionRepo(ctrl)

	tests := []struct {
		name    string
		ctx     context.Context
		mock    func()
		wantErr bool
	}{
		{
			name:    "no token in context",
			ctx:     context.Background(),
			mock:    func() {},
			wantErr: true,
		},
		{
			name: "delete session success",
			ctx: func() context.Context {
				token := newTestJWXToken()
				ctx := context.WithValue(context.Background(), jwtauth.TokenCtxKey, token)
				return ctx
			}(),
			mock: func() {
				mockCfg.EXPECT().
					Values().
					Return(config.Values{}).
					Times(0)
				mockSessionRepo.EXPECT().
					DeleteSession(gomock.Any(), gomock.Any()).
					Return(nil)
			},
			wantErr: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tt.mock()

			s := &LogoutServiceImplV2{
				BaseService: &BaseService{
					cfg: mockCfg,
				},
				sessionRepo: mockSessionRepo,
			}

			err := s.Logout(tt.ctx)
			assert.Equal(t, tt.wantErr, err != nil)
		})
	}
}
