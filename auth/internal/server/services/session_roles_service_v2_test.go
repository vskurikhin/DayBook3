package services

import (
	"context"
	"errors"
	"testing"

	"github.com/go-chi/jwtauth/v5"
	"github.com/golang-jwt/jwt/v5"
	jwx "github.com/lestrrat-go/jwx/v3/jwt"
	"github.com/stretchr/testify/require"
	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
)

func TestSessionRoles(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockRepo := NewMockSessionRepo(ctrl)

	service := &SessionRolesImplV2{
		BaseService: &BaseService{},
		sessionRepo: mockRepo,
	}

	validToken := newTestJWXToken()

	ctxWithToken := jwtauth.NewContext(context.Background(), validToken, nil)

	jwt.New(jwt.SigningMethodHS256)

	tests := []struct {
		name          string
		ctx           context.Context
		mockSetup     func()
		expectedError bool
	}{
		{
			name: "success",
			ctx:  ctxWithToken,
			mockSetup: func() {
				mockRepo.EXPECT().
					GetSession(gomock.Any(), gomock.Any()).
					Return(session.Session{}, nil)
			},
			expectedError: false,
		},
		{
			name:          "no token in context",
			ctx:           context.Background(),
			mockSetup:     func() {},
			expectedError: true,
		},
		{
			name: "invalid token (cannot decode session id)",
			ctx: jwtauth.NewContext(context.Background(),
				jwx.New(), nil),
			mockSetup:     func() {},
			expectedError: true,
		},
		{
			name: "repo error",
			ctx:  ctxWithToken,
			mockSetup: func() {
				mockRepo.EXPECT().
					GetSession(gomock.Any(), gomock.Any()).
					Return(session.Session{}, errors.New("db error"))
			},
			expectedError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tt.mockSetup()

			_, err := service.SessionRoles(tt.ctx)

			if tt.expectedError {
				require.Error(t, err)
			} else {
				require.NoError(t, err)
			}
		})
	}
}
