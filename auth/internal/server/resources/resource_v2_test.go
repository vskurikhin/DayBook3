package resources

import (
	"bytes"
	"encoding/json"
	"errors"
	"net/http"
	"net/http/httptest"
	"testing"

	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/dto"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
)

func TestV2_Ok(t *testing.T) {
	tests := []struct {
		name         string
		serviceResp  string
		expectedBody string
	}{
		{
			name:         "success",
			serviceResp:  "ok",
			expectedBody: `{"success":true,"data":[{"msg":"ok"}]}` + "\n",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockAuthServiceV2 := NewMockAuthServiceV2(ctrl)
			mockCfg := NewMockConfig(ctrl)
			mockOkServiceV2 := NewMockOkServiceV2(ctrl)
			mockOkServiceV2.EXPECT().Ok().Return(tt.serviceResp)
			mockListServiceV2 := NewMockListServiceV2(ctrl)
			mockLogoutServiceV2 := NewMockLogoutServiceV2(ctrl)
			mockRefreshServiceV2 := NewMockRefreshServiceV2(ctrl)
			mockRegisterServiceV2 := NewMockRegisterServiceV2(ctrl)
			mockSessionRolesV2 := NewMockSessionRolesV2(ctrl)

			v := NewV2(
				mockAuthServiceV2,
				mockCfg,
				mockListServiceV2,
				mockLogoutServiceV2,
				mockOkServiceV2,
				mockRefreshServiceV2,
				mockRegisterServiceV2,
				mockSessionRolesV2,
			)

			req := httptest.NewRequest(http.MethodGet, "/v2/ok", nil)
			rec := httptest.NewRecorder()

			err := v.Ok(rec, req)
			if err != nil {
				t.Fatalf("unexpected error: %v", err)
			}

			if rec.Body.String() != tt.expectedBody {
				t.Fatalf("expected %s, got %s", tt.expectedBody, rec.Body.String())
			}
		})
	}
}

func TestV2_Auth(t *testing.T) {
	tests := []struct {
		name        string
		body        any
		mockSetup   func(c *MockConfig, m *MockAuthServiceV2)
		expectError bool
	}{
		{
			name: "invalid json",
			body: "invalid",
			mockSetup: func(c *MockConfig, m *MockAuthServiceV2) {
				c.EXPECT().
					Values().
					Return(config.Values{RequestMaxBytes: 1 << 20}).
					Times(1)
			},
			expectError: true,
		},
		{
			name: "success",
			body: dto.Login{},
			mockSetup: func(c *MockConfig, m *MockAuthServiceV2) {
				c.EXPECT().
					Values().
					Return(config.Values{RequestMaxBytes: 1 << 20}).
					Times(1)
				m.EXPECT().
					Auth(gomock.Any(), gomock.Any()).
					Return(mockCredentials(), nil)
			},
			expectError: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockAuthServiceV2 := NewMockAuthServiceV2(ctrl)
			mockCfg := NewMockConfig(ctrl)
			tt.mockSetup(mockCfg, mockAuthServiceV2)
			mockOkServiceV2 := NewMockOkServiceV2(ctrl)
			mockListServiceV2 := NewMockListServiceV2(ctrl)
			mockLogoutServiceV2 := NewMockLogoutServiceV2(ctrl)
			mockRefreshServiceV2 := NewMockRefreshServiceV2(ctrl)
			mockRegisterServiceV2 := NewMockRegisterServiceV2(ctrl)
			mockSessionRolesV2 := NewMockSessionRolesV2(ctrl)

			v := NewV2(
				mockAuthServiceV2,
				mockCfg,
				mockListServiceV2,
				mockLogoutServiceV2,
				mockOkServiceV2,
				mockRefreshServiceV2,
				mockRegisterServiceV2,
				mockSessionRolesV2,
			)

			var bodyBytes []byte
			switch b := tt.body.(type) {
			case string:
				bodyBytes = []byte(b)
			default:
				bodyBytes, _ = json.Marshal(b)
			}

			req := httptest.NewRequest(http.MethodPost, "/auth", bytes.NewReader(bodyBytes))
			rec := httptest.NewRecorder()

			err := v.Auth(rec, req)

			if tt.expectError && err == nil {
				t.Fatal("expected error, got nil")
			}
			if !tt.expectError && err != nil {
				t.Fatalf("unexpected error: %v", err)
			}
		})
	}
}

func TestV2_Refresh(t *testing.T) {
	tests := []struct {
		name        string
		withCookie  bool
		mockSetup   func(c *MockConfig, m *MockRefreshServiceV2)
		expectError bool
	}{
		{
			name:       "no cookie",
			withCookie: false,
			mockSetup: func(c *MockConfig, m *MockRefreshServiceV2) {
				c.EXPECT().
					Values().
					Return(config.Values{RequestMaxBytes: 1 << 20}).
					Times(0)
			},
			expectError: true,
		},
		{
			name:       "success",
			withCookie: true,
			mockSetup: func(c *MockConfig, m *MockRefreshServiceV2) {
				c.EXPECT().
					Values().
					Return(config.Values{RequestMaxBytes: 1 << 20}).
					Times(1)
				m.EXPECT().
					Refresh(gomock.Any(), "token").
					Return(mockCredentials(), nil)
			},
			expectError: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockAuthServiceV2 := NewMockAuthServiceV2(ctrl)
			mockCfg := NewMockConfig(ctrl)
			mockOkServiceV2 := NewMockOkServiceV2(ctrl)
			mockListServiceV2 := NewMockListServiceV2(ctrl)
			mockLogoutServiceV2 := NewMockLogoutServiceV2(ctrl)
			mockRefreshServiceV2 := NewMockRefreshServiceV2(ctrl)
			tt.mockSetup(mockCfg, mockRefreshServiceV2)
			mockRegisterServiceV2 := NewMockRegisterServiceV2(ctrl)
			mockSessionRolesV2 := NewMockSessionRolesV2(ctrl)

			v := NewV2(
				mockAuthServiceV2,
				mockCfg,
				mockListServiceV2,
				mockLogoutServiceV2,
				mockOkServiceV2,
				mockRefreshServiceV2,
				mockRegisterServiceV2,
				mockSessionRolesV2,
			)

			req := httptest.NewRequest(http.MethodPost, "/refresh", bytes.NewReader([]byte(`{}`)))
			if tt.withCookie {
				req.AddCookie(&http.Cookie{Name: "refresh", Value: "token"})
			}

			rec := httptest.NewRecorder()

			err := v.Refresh(rec, req)

			if tt.expectError && err == nil {
				t.Fatal("expected error")
			}
			if !tt.expectError && err != nil {
				t.Fatalf("unexpected error: %v", err)
			}
		})
	}
}

func TestV2_List(t *testing.T) {
	tests := []struct {
		name        string
		mockSetup   func(m *MockListServiceV2)
		expectError bool
	}{
		{
			name: "success",
			mockSetup: func(m *MockListServiceV2) {
				m.EXPECT().List(gomock.Any()).Return([]model.User{}, nil)
			},
		},
		{
			name: "error",
			mockSetup: func(m *MockListServiceV2) {
				m.EXPECT().List(gomock.Any()).Return(nil, errors.New("fail"))
			},
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockAuthServiceV2 := NewMockAuthServiceV2(ctrl)
			mockCfg := NewMockConfig(ctrl)
			mockOkServiceV2 := NewMockOkServiceV2(ctrl)
			mockListServiceV2 := NewMockListServiceV2(ctrl)
			tt.mockSetup(mockListServiceV2)
			mockLogoutServiceV2 := NewMockLogoutServiceV2(ctrl)
			mockRefreshServiceV2 := NewMockRefreshServiceV2(ctrl)
			mockRegisterServiceV2 := NewMockRegisterServiceV2(ctrl)
			mockSessionRolesV2 := NewMockSessionRolesV2(ctrl)

			v := NewV2(
				mockAuthServiceV2,
				mockCfg,
				mockListServiceV2,
				mockLogoutServiceV2,
				mockOkServiceV2,
				mockRefreshServiceV2,
				mockRegisterServiceV2,
				mockSessionRolesV2,
			)

			req := httptest.NewRequest(http.MethodGet, "/user/list", nil)
			rec := httptest.NewRecorder()

			err := v.List(rec, req)

			if tt.expectError && err == nil {
				t.Fatal("expected error")
			}
		})
	}
}

func TestV2_Logout(t *testing.T) {
	tests := []struct {
		name        string
		mockSetup   func(m *MockLogoutServiceV2)
		expectError bool
	}{
		{
			name: "success",
			mockSetup: func(m *MockLogoutServiceV2) {
				m.EXPECT().Logout(gomock.Any()).Return(nil)
			},
		},
		{
			name: "error",
			mockSetup: func(m *MockLogoutServiceV2) {
				m.EXPECT().Logout(gomock.Any()).Return(errors.New("fail"))
			},
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockAuthServiceV2 := NewMockAuthServiceV2(ctrl)
			mockCfg := NewMockConfig(ctrl)
			mockOkServiceV2 := NewMockOkServiceV2(ctrl)
			mockListServiceV2 := NewMockListServiceV2(ctrl)
			mockLogoutServiceV2 := NewMockLogoutServiceV2(ctrl)
			tt.mockSetup(mockLogoutServiceV2)
			mockRefreshServiceV2 := NewMockRefreshServiceV2(ctrl)
			mockRegisterServiceV2 := NewMockRegisterServiceV2(ctrl)
			mockSessionRolesV2 := NewMockSessionRolesV2(ctrl)

			v := NewV2(
				mockAuthServiceV2,
				mockCfg,
				mockListServiceV2,
				mockLogoutServiceV2,
				mockOkServiceV2,
				mockRefreshServiceV2,
				mockRegisterServiceV2,
				mockSessionRolesV2,
			)

			req := httptest.NewRequest(http.MethodPost, "/logout", nil)
			rec := httptest.NewRecorder()

			err := v.Logout(rec, req)

			if tt.expectError && err == nil {
				t.Fatal("expected error")
			}
		})
	}
}

func TestV2_Register(t *testing.T) {
	tests := []struct {
		name        string
		body        any
		mockSetup   func(c *MockConfig, m *MockRegisterServiceV2)
		expectError bool
	}{
		{
			name: "invalid json",
			body: "bad",
			mockSetup: func(c *MockConfig, m *MockRegisterServiceV2) {
				c.EXPECT().
					Values().
					Return(config.Values{RequestMaxBytes: 1 << 20}).
					Times(1)
			},
			expectError: true,
		},
		{
			name: "success",
			body: dto.CreateUser{},
			mockSetup: func(c *MockConfig, m *MockRegisterServiceV2) {
				c.EXPECT().
					Values().
					Return(config.Values{RequestMaxBytes: 1 << 20}).
					Times(1)
				m.EXPECT().
					Register(gomock.Any(), gomock.Any()).
					Return(mockCredentials(), nil)
			},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockAuthServiceV2 := NewMockAuthServiceV2(ctrl)
			mockCfg := NewMockConfig(ctrl)
			mockOkServiceV2 := NewMockOkServiceV2(ctrl)
			mockListServiceV2 := NewMockListServiceV2(ctrl)
			mockLogoutServiceV2 := NewMockLogoutServiceV2(ctrl)
			mockRefreshServiceV2 := NewMockRefreshServiceV2(ctrl)
			mockRegisterServiceV2 := NewMockRegisterServiceV2(ctrl)
			mockSessionRolesV2 := NewMockSessionRolesV2(ctrl)
			tt.mockSetup(mockCfg, mockRegisterServiceV2)

			v := NewV2(
				mockAuthServiceV2,
				mockCfg,
				mockListServiceV2,
				mockLogoutServiceV2,
				mockOkServiceV2,
				mockRefreshServiceV2,
				mockRegisterServiceV2,
				mockSessionRolesV2,
			)

			var bodyBytes []byte
			switch b := tt.body.(type) {
			case string:
				bodyBytes = []byte(b)
			default:
				bodyBytes, _ = json.Marshal(b)
			}

			req := httptest.NewRequest(http.MethodPost, "/register", bytes.NewReader(bodyBytes))
			rec := httptest.NewRecorder()

			err := v.Register(rec, req)

			if tt.expectError && err == nil {
				t.Fatal("expected error")
			}
		})
	}
}

func TestV2_SessionRoles(t *testing.T) {
	tests := []struct {
		name        string
		mockSetup   func(m *MockSessionRolesV2)
		expectError bool
	}{
		{
			name: "success",
			mockSetup: func(m *MockSessionRolesV2) {
				m.EXPECT().SessionRoles(gomock.Any()).Return(model.UserHasRolesFromModelSession(session.Session{
					Roles: []string{},
				}), nil)
			},
		},
		{
			name: "error",
			mockSetup: func(m *MockSessionRolesV2) {
				m.EXPECT().SessionRoles(gomock.Any()).Return(model.UserHasRoles{}, errors.New("fail"))
			},
			expectError: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockAuthServiceV2 := NewMockAuthServiceV2(ctrl)
			mockCfg := NewMockConfig(ctrl)
			mockOkServiceV2 := NewMockOkServiceV2(ctrl)
			mockListServiceV2 := NewMockListServiceV2(ctrl)
			mockLogoutServiceV2 := NewMockLogoutServiceV2(ctrl)
			mockRefreshServiceV2 := NewMockRefreshServiceV2(ctrl)
			mockRegisterServiceV2 := NewMockRegisterServiceV2(ctrl)
			mockSessionRolesV2 := NewMockSessionRolesV2(ctrl)
			tt.mockSetup(mockSessionRolesV2)

			v := NewV2(
				mockAuthServiceV2,
				mockCfg,
				mockListServiceV2,
				mockLogoutServiceV2,
				mockOkServiceV2,
				mockRefreshServiceV2,
				mockRegisterServiceV2,
				mockSessionRolesV2,
			)

			req := httptest.NewRequest(http.MethodGet, "/session/roles", nil)
			rec := httptest.NewRecorder()

			err := v.SessionRoles(rec, req)

			if tt.expectError && err == nil {
				t.Fatal("expected error")
			}
		})
	}
}

func mockCredentials() model.Credentials {
	return model.Credentials{}
}
