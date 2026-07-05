package services

import (
	"context"
	"errors"
	"testing"
	"time"

	"github.com/golang-jwt/jwt/v5"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/creds"
)

// --- tests ---

func TestRefreshServiceImplV2_Refresh(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockSessionRepo := NewMockSessionRepo(ctrl)
	mockUserViewRepo := NewMockUserViewRepo(ctrl)

	cfg := NewMockConfig(ctrl)
	cfg.EXPECT().Values().Return(config.Values{
		JWThs256SignKey:        []byte("secret"),
		ValidPeriodAccessToken: time.Minute,
	}).AnyTimes()

	tests := []struct {
		name    string
		token   string
		mock    func()
		wantErr bool
	}{
		{
			name:    "invalid token",
			token:   "bad-token",
			mock:    func() {},
			wantErr: true,
		},
		{
			name: "session not found",
			token: func() string {
				tk := jwt.New(jwt.SigningMethodHS256)
				s, _ := tk.SignedString([]byte("secret"))
				return s
			}(),
			mock: func() {
				mockSessionRepo.EXPECT().
					GetSession(gomock.Any(), gomock.Any()).
					Return(session.Session{}, errors.New("err"))
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tt.mock()

			s := &RefreshServiceImplV2{
				BaseService:        &BaseService{cfg: cfg},
				credentialsFactory: creds.NewCredentialsMethodFactory(cfg),
				sessionRepo:        mockSessionRepo,
				userViewRepo:       mockUserViewRepo,
			}

			_, err := s.Refresh(context.Background(), tt.token)
			assert.Equal(t, tt.wantErr, err != nil)
		})
	}
}

func TestRefreshServiceImplV2_refresh(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockSessionRepo := NewMockSessionRepo(ctrl)

	cfg := NewMockConfig(ctrl)
	cfg.EXPECT().Values().Return(config.Values{
		ValidPeriodAccessToken: time.Minute,
	}).AnyTimes()

	tests := []struct {
		name    string
		claims  jwt.Claims
		mock    func()
		wantErr bool
	}{
		{
			name:    "invalid claims",
			claims:  jwt.MapClaims{},
			mock:    func() {},
			wantErr: true,
		},
		{
			name: "session repo error",
			claims: jwt.MapClaims{
				"iss": "test",
				"sub": "test",
				"jti": "test",
			},
			mock: func() {
				mockSessionRepo.EXPECT().
					GetSession(gomock.Any(), gomock.Any()).
					Return(session.Session{}, errors.New("err")).
					AnyTimes()
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tt.mock()

			s := &RefreshServiceImplV2{
				BaseService:        &BaseService{cfg: cfg},
				credentialsFactory: creds.NewCredentialsMethodFactory(cfg),
				sessionRepo:        mockSessionRepo,
			}

			_, err := s.refresh(context.Background(), tt.claims)
			assert.Equal(t, tt.wantErr, err != nil)
		})
	}
}

func TestRefresh_InvalidToken(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockCfg := NewMockConfig(ctrl)

	s := &RefreshServiceImplV2{
		BaseService: &BaseService{cfg: mockCfg},
	}

	_, err := s.Refresh(context.Background(), "invalid-token")

	require.Error(t, err)
}

func TestRefresh_SessionRepoError(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockCfg := NewMockConfig(ctrl)
	mockCfg.EXPECT().
		Values().
		Return(config.Values{}).
		Times(0)
	mockSessionRepo := NewMockSessionRepo(ctrl)
	mockUserViewRepo := NewMockUserViewRepo(ctrl)

	claims := jwt.MapClaims{
		"iss": "test",
		"sub": "test",
		"jti": "test",
	}

	s := &RefreshServiceImplV2{
		BaseService:        &BaseService{cfg: mockCfg},
		credentialsFactory: creds.NewCredentialsMethodFactory(mockCfg),
		sessionRepo:        mockSessionRepo,
		userViewRepo:       mockUserViewRepo,
	}

	_, err := s.refresh(context.Background(), claims)

	require.Error(t, err)
}
