package services

import (
	"context"
	"errors"
	"strings"
	"testing"

	"github.com/jackc/pgx/v5/pgtype"
	"github.com/stretchr/testify/require"
	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/actions"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/dto"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_attrs"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_name"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/creds"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
)

func TestRegisterService_Register(t *testing.T) {
	var (
		mockCfg               *MockConfig
		mockDBPool            *MockDB
		mockDBTXSessionRepo   *MockDBTX
		mockDBTXUserAttrsRepo *MockDBTX
		mockDBTXUserNameRepo  *MockDBTX
		mockSessionRepo       *MockSessionRepo
		mockTx                *MockTx
		mockUserAttrsRepo     *MockUserAttrsRepo
		mockUserNameRepo      *MockUserNameRepo
	)
	tests := []struct {
		name        string
		setupMocks  func()
		user        model.CreateUser
		expectError bool
	}{
		{
			name: "hash error",
			setupMocks: func() {
				// Специально делаем невалидный cost → Hash вернёт ошибку
				mockCfg.EXPECT().
					Values().
					Return(config.Values{
						AuthCost: 255,
					}).AnyTimes()
			},
			user: model.CreateUserFromDto(dto.CreateUser{
				Password:        strings.Repeat("a", 73),
				ConfirmPassword: strings.Repeat("a", 73),
			}),
			expectError: true,
		},
		{
			name: "success",
			setupMocks: func() {
				// Специально делаем невалидный cost → Hash вернёт ошибку
				mockCfg.EXPECT().Values().AnyTimes().Return(config.Values{
					AuthCost:        1,
					JWThs256SignKey: []byte("secret"),
				}).Times(5)
				mockDBPool.EXPECT().
					Begin(gomock.Any()).
					Return(mockTx, nil).
					Times(1)

				mockRowDBTXSessionRepo := &mockRowSession{data: session.Session{}}
				mockDBTXSessionRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXSessionRepo).
					Times(1)
				mockSessionRepo.EXPECT().
					WithTx(gomock.Any()).
					Return(session.New(mockDBTXSessionRepo)).
					Times(1)

				mockRowDBTXUserAttrsRepo := &mockRowUserAttrs{data: user_attrs.UserAttr{}}
				mockDBTXUserAttrsRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXUserAttrsRepo).
					Times(1)
				mockUserAttrsRepo.EXPECT().
					WithTx(gomock.Any()).
					Return(user_attrs.New(mockDBTXUserAttrsRepo)).
					Times(1)

				mockRowDBTXUserNameRepo := &mockRowUserName{data: user_name.UserName{ID: pgtype.UUID{Valid: true}}}
				mockDBTXUserNameRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXUserNameRepo).
					Times(1)
				mockUserNameRepo.EXPECT().
					WithTx(gomock.Any()).
					Return(user_name.New(mockDBTXUserNameRepo)).
					Times(1)

				mockTx.EXPECT().
					Commit(gomock.Any()).
					Return(nil).
					Times(1)
			},
			user:        model.CreateUser{},
			expectError: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockCfg = NewMockConfig(ctrl)
			mockDBPool = NewMockDB(ctrl)
			mockTx = NewMockTx(ctrl)
			mockSessionRepo = NewMockSessionRepo(ctrl)
			mockUserAttrsRepo = NewMockUserAttrsRepo(ctrl)
			mockUserNameRepo = NewMockUserNameRepo(ctrl)
			mockDBTXSessionRepo = NewMockDBTX(ctrl)
			mockDBTXUserAttrsRepo = NewMockDBTX(ctrl)
			mockDBTXUserNameRepo = NewMockDBTX(ctrl)

			tt.setupMocks()

			credsFactory := creds.NewCredentialsMethodFactory(mockCfg)
			service := NewRegisterServiceV2(
				mockCfg, credsFactory, mockDBPool, mockSessionRepo,
				&actions.TransactionDelayer{}, mockUserAttrsRepo, mockUserNameRepo,
			)

			_, err := service.Register(context.Background(), tt.user)

			if tt.expectError {
				require.Error(t, err)
			} else {
				require.NoError(t, err)
			}
		})
	}
}

func TestRegisterService_transactionRegister(t *testing.T) {
	var (
		mockCfg               *MockConfig
		mockDBPool            *MockDB
		mockDBTXSessionRepo   *MockDBTX
		mockDBTXUserAttrsRepo *MockDBTX
		mockDBTXUserNameRepo  *MockDBTX
		mockSessionRepo       *MockSessionRepo
		mockTx                *MockTx
		mockTxDelayer         *MockTxDelayer
		mockUserAttrsRepo     *MockUserAttrsRepo
		mockUserNameRepo      *MockUserNameRepo
	)

	tests := []struct {
		name        string
		setupMocks  func(context.Context)
		expectError bool
	}{
		{
			name: "begin tx error",
			setupMocks: func(ctx context.Context) {
				mockCfg.EXPECT().Values().AnyTimes().Return(config.Values{
					Hostname:                "test-host",
					ValidPeriodAccessToken:  1,
					ValidPeriodRefreshToken: 1,
				})
				mockDBPool.EXPECT().Begin(ctx).Return(nil, errors.New("db error"))
			},
			expectError: true,
		},
		{
			name: "user exists",
			setupMocks: func(ctx context.Context) {
				mockCfg.EXPECT().Values().AnyTimes().Return(config.Values{
					Hostname:                "test-host",
					ValidPeriodAccessToken:  1,
					ValidPeriodRefreshToken: 1,
				})
				mockDBPool.EXPECT().Begin(ctx).Return(mockTx, nil)

				mockDBTXUserNameRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(&mockRowUserNameError{}).
					Times(1)

				mockUserNameRepo.EXPECT().
					WithTx(mockTx).
					Return(user_name.New(mockDBTXUserNameRepo)).
					AnyTimes()

				mockSessionRepo.EXPECT().
					WithTx(gomock.Any()).
					Return(session.New(nil)).
					Times(1)

				mockUserAttrsRepo.EXPECT().
					WithTx(gomock.Any()).
					Return(user_attrs.New(nil)).
					Times(1)

				mockTxDelayer.EXPECT().
					Defer(ctx, mockTx, gomock.Any()).
					AnyTimes()
			},
			expectError: true,
		},
		{
			name: "success",
			setupMocks: func(ctx context.Context) {
				mockCfg.EXPECT().Values().AnyTimes().Return(config.Values{
					AuthCost:        1,
					JWThs256SignKey: []byte("secret"),
				}).Times(1)
				mockDBPool.EXPECT().
					Begin(gomock.Any()).
					Return(mockTx, nil).
					Times(1)

				mockRowDBTXSessionRepo := &mockRowSession{data: session.Session{}}
				mockDBTXSessionRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXSessionRepo).
					Times(1)
				mockSessionRepo.EXPECT().
					WithTx(gomock.Any()).
					Return(session.New(mockDBTXSessionRepo)).
					Times(1)

				mockRowDBTXUserAttrsRepo := &mockRowUserAttrs{data: user_attrs.UserAttr{}}
				mockDBTXUserAttrsRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXUserAttrsRepo).
					Times(1)
				mockUserAttrsRepo.EXPECT().
					WithTx(gomock.Any()).
					Return(user_attrs.New(mockDBTXUserAttrsRepo)).
					Times(1)

				mockRowDBTXUserNameRepo := &mockRowUserName{data: user_name.UserName{ID: pgtype.UUID{Valid: true}}}
				mockDBTXUserNameRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXUserNameRepo).
					Times(1)
				mockUserNameRepo.EXPECT().
					WithTx(gomock.Any()).
					Return(user_name.New(mockDBTXUserNameRepo)).
					Times(1)

				mockTxDelayer.EXPECT().
					Defer(ctx, mockTx, gomock.Any()).
					Times(1)
			},
			expectError: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockCfg = NewMockConfig(ctrl)
			mockDBPool = NewMockDB(ctrl)
			mockTx = NewMockTx(ctrl)
			mockSessionRepo = NewMockSessionRepo(ctrl)
			mockUserAttrsRepo = NewMockUserAttrsRepo(ctrl)
			mockUserNameRepo = NewMockUserNameRepo(ctrl)
			mockTxDelayer = NewMockTxDelayer(ctrl)
			mockDBTXSessionRepo = NewMockDBTX(ctrl)
			mockDBTXUserAttrsRepo = NewMockDBTX(ctrl)
			mockDBTXUserNameRepo = NewMockDBTX(ctrl)

			ctx := context.Background()

			tt.setupMocks(ctx)

			credsFactory := creds.NewCredentialsMethodFactory(mockCfg)
			service := NewRegisterServiceV2(
				mockCfg, credsFactory, mockDBPool, mockSessionRepo,
				mockTxDelayer, mockUserAttrsRepo, mockUserNameRepo,
			)

			_, err := service.transactionRegister(ctx, model.CreateUser{})

			if tt.expectError {
				require.Error(t, err)
			} else {
				require.NoError(t, err)
			}
		})
	}
}
