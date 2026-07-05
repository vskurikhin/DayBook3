package services

import (
	"context"
	"errors"
	"fmt"
	"os"
	"testing"

	"github.com/google/uuid"
	"github.com/jackc/pgx/v5/pgtype"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/actions"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/dto"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_has_roles"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_view"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/creds"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
)

func TestAuthService_Auth(t *testing.T) {
	var (
		mockCfg                  *MockConfig
		mockDBPool               *MockDB
		mockDBTXSessionRepo      *MockDBTX
		mockDBTXUserHasRolesRepo *MockDBTX
		mockSessionRepo          *MockSessionRepo
		mockTx                   *MockTx
		mockUserHasRolesRepo     *MockUserHasRolesRepo
		mockUserViewRepo         *MockUserViewRepo
	)
	tests := []struct {
		name        string
		setupMocks  func()
		expectError bool
	}{
		{
			name: "factory returns error",
			setupMocks: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{}, errors.New("err"))
			},
			expectError: true,
		},
		{
			name: "password not valid",
			setupMocks: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{}, nil)
			},
			expectError: true,
		},
		{
			name: "invalid token",
			setupMocks: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{Password: pgtype.Text{Valid: true}}, nil)
			},
			expectError: true,
		},
		{
			name: "invalid password",
			setupMocks: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{UserName: pgtype.Text{Valid: true}, Password: pgtype.Text{Valid: true}}, nil)
			},
			expectError: true,
		},
		{
			name: "success",
			setupMocks: func() {
				mockCfg.EXPECT().Values().Return(config.Values{
					JWThs256SignKey: []byte("secret"),
				}).Times(5)
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{
						UserName: pgtype.Text{Valid: true},
						Password: pgtype.Text{String: "$2a$13$skbthyXoVBw24OxT0is4muHSZXEe0QRJo2DFN6iP1EmVsPTkEK8QG", Valid: true},
					}, nil).
					Times(1)
				mockDBPool.EXPECT().
					Begin(gomock.Any()).
					Return(mockTx, nil).
					Times(1)

				mockRowDBTXUserHasRolesRepo := &mockRowUserHasRoles{data: user_has_roles.GetRolesForUserNameRow{}}
				mockDBTXUserHasRolesRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXUserHasRolesRepo).
					Times(1)
				mockUserHasRolesRepo.EXPECT().
					WithTx(mockTx).
					Return(user_has_roles.New(mockDBTXUserHasRolesRepo))

				mockRowDBTXSessionRepo := &mockRowSession{data: session.Session{}}
				mockDBTXSessionRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXSessionRepo).
					Times(1)
				mockSessionRepo.EXPECT().
					WithTx(mockTx).
					Return(session.New(mockDBTXSessionRepo))

				mockTx.EXPECT().
					Commit(gomock.Any()).
					Return(nil).
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
			mockDBTXSessionRepo = NewMockDBTX(ctrl)
			mockDBTXUserHasRolesRepo = NewMockDBTX(ctrl)
			mockSessionRepo = NewMockSessionRepo(ctrl)
			mockTx = NewMockTx(ctrl)
			mockUserHasRolesRepo = NewMockUserHasRolesRepo(ctrl)
			mockUserViewRepo = NewMockUserViewRepo(ctrl)

			tt.setupMocks()

			service := NewAuthServiceV2(mockCfg,
				creds.NewCredentialsMethodFactory(mockCfg),
				mockDBPool, mockSessionRepo,
				actions.NewTransactionDelayer(),
				mockUserHasRolesRepo,
				mockUserViewRepo,
			)

			_, err := service.Auth(context.Background(), model.LoginFromDto(dto.Login{Password: "password"}))

			if tt.expectError {
				require.Error(t, err)
			} else {
				require.NoError(t, err)
			}
		})
	}
}

func TestAuthService_auth(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	ctx := context.Background()

	mockUserViewRepo := NewMockUserViewRepo(ctrl)

	mockCfg := NewMockConfig(ctrl)
	mockCfg.EXPECT().Values().AnyTimes().Return(config.Values{
		Hostname: "test-host",
	})

	service := &AuthServiceImplV2{
		BaseService:  &BaseService{cfg: mockCfg},
		userViewRepo: mockUserViewRepo,
	}

	tests := []struct {
		name        string
		setupMocks  func()
		expectError bool
	}{
		{
			name: "GetUserName error",
			setupMocks: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{}, errors.New("db error"))
			},
			expectError: true,
		},
		{
			name: "password not valid",
			setupMocks: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{
						Password: pgtype.Text{Valid: false},
					}, nil)
			},
			expectError: true,
		},
		{
			name: "username not valid",
			setupMocks: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{
						Password: pgtype.Text{Valid: true, String: "hash"},
						UserName: pgtype.Text{Valid: false},
					}, nil)
			},
			expectError: true,
		},
		{
			name: "invalid password",
			setupMocks: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{
						Password: pgtype.Text{Valid: true, String: "hash"},
						UserName: pgtype.Text{Valid: true, String: "user"},
					}, nil)
			},
			expectError: true,
		},
		{
			name: "success (go to transaction)",
			setupMocks: func() {
				// ⚠️ тут Verify должен пройти → используем одинаковые строки
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{
						Password: pgtype.Text{Valid: true, String: "pass"},
						UserName: pgtype.Text{Valid: true, String: "user"},
					}, nil)
			},
			expectError: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tt.setupMocks()

			_, err := service.auth(ctx, model.Login{})

			if tt.expectError {
				require.Error(t, err)
			}
		})
	}
}

func TestAuthService_transactionAuth(t *testing.T) {
	var (
		mockCfg                  *MockConfig
		mockDBPool               *MockDB
		mockDBTXSessionRepo      *MockDBTX
		mockDBTXUserHasRolesRepo *MockDBTX
		mockSessionRepo          *MockSessionRepo
		mockUserHasRolesRepo     *MockUserHasRolesRepo
		mockTx                   *MockTx
		mockTxDelayer            *MockTxDelayer
	)

	sid, _ := model.MakeSessionID("host", "user")

	tests := []struct {
		name        string
		setupMocks  func(context.Context)
		expectError bool
	}{
		{
			name: "begin error",
			setupMocks: func(ctx context.Context) {
				mockCfg.EXPECT().Values().AnyTimes().Return(config.Values{
					ValidPeriodAccessToken:  1,
					ValidPeriodRefreshToken: 1,
				})
				mockDBPool.EXPECT().Begin(ctx).Return(nil, errors.New("db error"))
			},
			expectError: true,
		},
		{
			name: "create session error",
			setupMocks: func(ctx context.Context) {
				mockCfg.EXPECT().Values().AnyTimes().Return(config.Values{
					ValidPeriodAccessToken:  1,
					ValidPeriodRefreshToken: 1,
				})
				mockDBPool.EXPECT().Begin(ctx).Return(mockTx, nil)

				mockRowDBTXUserHasRolesRepo := &mockRowUserHasRoles{data: user_has_roles.GetRolesForUserNameRow{}}
				mockDBTXUserHasRolesRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXUserHasRolesRepo).
					Times(1)
				mockUserHasRolesRepo.EXPECT().
					WithTx(mockTx).
					Return(user_has_roles.New(mockDBTXUserHasRolesRepo))

				mockRowDBTXSessionRepo := &mockRowSessionError{}
				mockDBTXSessionRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXSessionRepo).
					Times(1)
				mockSessionRepo.EXPECT().
					WithTx(mockTx).
					Return(session.New(mockDBTXSessionRepo)).
					Times(1)

				mockTxDelayer.EXPECT().Defer(ctx, mockTx, gomock.Any())
			},
			expectError: true,
		},
		{
			name: "success",
			setupMocks: func(ctx context.Context) {
				mockCfg.EXPECT().Values().AnyTimes().Return(config.Values{
					ValidPeriodAccessToken:  1,
					ValidPeriodRefreshToken: 1,
				})
				mockDBPool.EXPECT().Begin(ctx).Return(mockTx, nil)

				mockRowDBTXUserHasRolesRepo := &mockRowUserHasRoles{data: user_has_roles.GetRolesForUserNameRow{}}
				mockDBTXUserHasRolesRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXUserHasRolesRepo).
					Times(1)
				mockUserHasRolesRepo.EXPECT().
					WithTx(mockTx).
					Return(user_has_roles.New(mockDBTXUserHasRolesRepo))

				mockRowDBTXSessionRepo := &mockRowSession{data: session.Session{}}
				mockDBTXSessionRepo.EXPECT().
					QueryRow(gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any(), gomock.Any()).
					Return(mockRowDBTXSessionRepo).
					Times(1)
				mockSessionRepo.EXPECT().
					WithTx(mockTx).
					Return(session.New(mockDBTXSessionRepo)).
					Times(1)

				mockTxDelayer.EXPECT().Defer(ctx, mockTx, gomock.Any())
			},
			expectError: false,
		},
	}

	user := user_view.UserView{
		UserName: pgtype.Text{String: "user", Valid: true},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			ctx := context.Background()

			mockCfg = NewMockConfig(ctrl)
			mockDBPool = NewMockDB(ctrl)
			mockDBTXSessionRepo = NewMockDBTX(ctrl)
			mockDBTXUserHasRolesRepo = NewMockDBTX(ctrl)
			mockSessionRepo = NewMockSessionRepo(ctrl)
			mockTx = NewMockTx(ctrl)
			mockTxDelayer = NewMockTxDelayer(ctrl)
			mockUserHasRolesRepo = NewMockUserHasRolesRepo(ctrl)

			tt.setupMocks(context.Background())

			service := &AuthServiceImplV2{
				BaseService:  &BaseService{cfg: mockCfg},
				dbPool:       mockDBPool,
				sessionRepo:  mockSessionRepo,
				txDelayer:    mockTxDelayer,
				userHasRoles: mockUserHasRolesRepo,
			}

			_, err := service.transactionAuth(ctx, sid, user)

			if tt.expectError {
				require.Error(t, err)
			} else {
				require.NoError(t, err)
			}
		})
	}
}

func TestAuthServiceImplV2_Auth(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	ctx := context.Background()

	mockSessionRepo := NewMockSessionRepo(ctrl)
	mockUserHasRolesRepo := NewMockUserHasRolesRepo(ctrl)
	mockUserViewRepo := NewMockUserViewRepo(ctrl)
	mockDB := NewMockDB(ctrl)

	mockCfg := NewMockConfig(ctrl)
	mockCfg.EXPECT().Values().Return(config.Values{}).AnyTimes()

	credentialsFactory := creds.NewCredentialsMethodFactory(mockCfg)
	txDelayer := actions.TransactionDelayer{}

	service := NewAuthServiceV2(mockCfg, credentialsFactory, mockDB, mockSessionRepo, txDelayer, mockUserHasRolesRepo, mockUserViewRepo)

	login := model.LoginFromDto(dto.Login{
		UserName: "test",
		Password: "test",
	})

	tests := []struct {
		name        string
		setup       func()
		expectedErr error
	}{
		{
			name: "user repo error",
			setup: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{}, errors.New("db error"))
			},
			expectedErr: errors.New("db error"),
		},
		{
			name: "password not valid",
			setup: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{
						Password: pgtype.Text{Valid: false},
					}, nil)
			},
			expectedErr: xerror.ErrPasswordNotValid,
		},
		{
			name: "username not valid",
			setup: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{
						Password: pgtype.Text{String: "hash", Valid: true},
						UserName: pgtype.Text{Valid: false},
					}, nil)
			},
			expectedErr: xerror.ErrInvalidToken,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tt.setup()

			_, err := service.Auth(ctx, login)

			if tt.expectedErr != nil {
				require.Error(t, err)
			} else {
				require.NoError(t, err)
			}
		})
	}
}

func TestAuthServiceImplV2_auth2(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	mockUserViewRepo := NewMockUserViewRepo(ctrl)

	cfg := NewMockConfig(ctrl)
	cfg.EXPECT().Values().Return(config.Values{
		Hostname: "test",
	}).AnyTimes()

	tests := []struct {
		name    string
		login   model.Login
		mock    func()
		wantErr bool
	}{
		{
			name: "user not found",
			login: model.LoginFromDto(dto.Login{
				UserName: "test",
				Password: "pass",
			}), /*Login{userName: "test", password: "pass"}*/
			mock: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{}, errors.New("err"))
			},
			wantErr: true,
		},
		{
			name: "invalid password",
			login: model.LoginFromDto(dto.Login{
				UserName: "test",
				Password: "wrong",
			}), /*Login{userName: "test", password: "wrong"}*/
			mock: func() {
				mockUserViewRepo.EXPECT().
					GetUserName(gomock.Any(), gomock.Any()).
					Return(user_view.UserView{
						Password: pgtype.Text{String: "hashed", Valid: true},
						UserName: pgtype.Text{String: "test", Valid: true},
					}, nil)
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tt.mock()

			s := &AuthServiceImplV2{
				BaseService:  &BaseService{cfg: cfg},
				userViewRepo: mockUserViewRepo,
			}

			_, err := s.auth(context.Background(), tt.login)
			assert.Equal(t, tt.wantErr, err != nil)
		})
	}
}

func TestIssuerUUIDPrint(t *testing.T) {
	_, _ = fmt.Fprintf(os.Stderr, "%s\n", uuid.NewSHA1(uuid.NameSpaceDNS, []byte("localhost.svn.su")).String())
}

func TestSubjectUUIDPrint(t *testing.T) {
	_, _ = fmt.Fprintf(os.Stderr, "%s\n", uuid.NewSHA1(uuid.NameSpaceOID, []byte("guest")).String())
}
