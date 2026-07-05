package handler

import (
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/golang-jwt/jwt/v5"
	"github.com/stretchr/testify/assert"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"go.uber.org/mock/gomock"
)

func TestApiV2(t *testing.T) {
	type wantType struct {
		code int
		body string
	}
	type testType struct {
		name     string
		disable  bool
		newFunc  func(t gomock.TestReporter, opts ...gomock.ControllerOption) (ApiV2, *mockResourceV2Ok, *gomock.Controller)
		testFunc func(t *testing.T, router ApiV2) *httptest.ResponseRecorder
		want     wantType
	}
	tests := []testType{
		{
			name: "positive #1 ApiV2 route: " + OkURL,
			newFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (ApiV2, *mockResourceV2Ok, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				mock := &mockResourceV2Ok{}

				cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

				return NewApiV2(cfgMock, mock), mock, ctrl
			},
			testFunc: func(t *testing.T, router ApiV2) *httptest.ResponseRecorder {
				req := httptest.NewRequest(http.MethodGet, OkURL, nil)
				return testServeHTTP(router, req)
			},
			want: wantType{
				code: http.StatusOK,
				body: "ok",
			},
		},
		{
			name: "positive #2 route: " + AuthURL,
			newFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (ApiV2, *mockResourceV2Ok, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				mock := &mockResourceV2Ok{}

				cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

				return NewApiV2(cfgMock, mock), mock, ctrl
			},
			testFunc: func(t *testing.T, router ApiV2) *httptest.ResponseRecorder {
				req := httptest.NewRequest(http.MethodPost, AuthURL, nil)
				return testServeHTTP(router, req)
			},
			want: wantType{
				code: http.StatusOK,
				body: "auth",
			},
		},
		{
			name: "positive #3 route: " + UserListURL,
			newFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (ApiV2, *mockResourceV2Ok, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				mock := &mockResourceV2Ok{}

				cfgMock.EXPECT().Values().Return(config.Values{JWThs256SignKey: []byte("secret")}).AnyTimes()

				return NewApiV2(cfgMock, mock), mock, ctrl
			},
			testFunc: func(t *testing.T, router ApiV2) *httptest.ResponseRecorder {
				req := httptest.NewRequest(http.MethodGet, UserListURL, nil)
				token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{})
				st, _ := token.SignedString([]byte("secret"))
				req.Header.Set("Authorization", "Bearer "+string(st))
				return testServeHTTP(router, req)
			},
			want: wantType{
				code: http.StatusOK,
				body: "list",
			},
		},
		{
			name: "positive #4 route: " + LogoutURL,
			newFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (ApiV2, *mockResourceV2Ok, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				mock := &mockResourceV2Ok{}

				cfgMock.EXPECT().Values().Return(config.Values{JWThs256SignKey: []byte("secret")}).AnyTimes()

				return NewApiV2(cfgMock, mock), mock, ctrl
			},
			testFunc: func(t *testing.T, router ApiV2) *httptest.ResponseRecorder {
				req := httptest.NewRequest(http.MethodPost, LogoutURL, nil)
				token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{})
				st, _ := token.SignedString([]byte("secret"))
				req.Header.Set("Authorization", "Bearer "+string(st))
				return testServeHTTP(router, req)
			},
			want: wantType{
				code: http.StatusOK,
				body: "logout",
			},
		},
		{
			name: "positive #5 route: " + RefreshURL,
			newFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (ApiV2, *mockResourceV2Ok, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				mock := &mockResourceV2Ok{}

				cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

				return NewApiV2(cfgMock, mock), mock, ctrl
			},
			testFunc: func(t *testing.T, router ApiV2) *httptest.ResponseRecorder {
				req := httptest.NewRequest(http.MethodPost, RefreshURL, nil)
				return testServeHTTP(router, req)
			},
			want: wantType{
				code: http.StatusOK,
				body: "refresh",
			},
		},
		{
			name: "positive #6 route: " + RegisterURL,
			newFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (ApiV2, *mockResourceV2Ok, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				mock := &mockResourceV2Ok{}

				cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

				return NewApiV2(cfgMock, mock), mock, ctrl
			},
			testFunc: func(t *testing.T, router ApiV2) *httptest.ResponseRecorder {
				req := httptest.NewRequest(http.MethodPost, RegisterURL, nil)
				return testServeHTTP(router, req)
			},
			want: wantType{
				code: http.StatusOK,
				body: "register",
			},
		},
		{
			name: "positive #7 route: " + SessionRolesURL,
			newFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (ApiV2, *mockResourceV2Ok, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				mock := &mockResourceV2Ok{}

				cfgMock.EXPECT().Values().Return(config.Values{JWThs256SignKey: []byte("secret")}).AnyTimes()

				return NewApiV2(cfgMock, mock), mock, ctrl
			},
			testFunc: func(t *testing.T, router ApiV2) *httptest.ResponseRecorder {
				req := httptest.NewRequest(http.MethodGet, SessionRolesURL, nil)
				token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{})
				st, _ := token.SignedString([]byte("secret"))
				req.Header.Set("Authorization", "Bearer "+string(st))
				return testServeHTTP(router, req)
			},
			want: wantType{
				code: http.StatusOK,
				body: "session roles",
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if !tt.disable {
				router, mock, ctrl := tt.newFunc(t)
				defer ctrl.Finish()
				got := tt.testFunc(t, router)
				assert.Equal(t, tt.want.code, got.Code)
				assert.Equal(t, tt.want.body, got.Body.String())
				assert.True(t, mock.called)
			}
		})
	}
}

func testServeHTTP(router ApiV2, req *http.Request) *httptest.ResponseRecorder {
	rec := httptest.NewRecorder()
	router.ServeHTTP(rec, req)
	return rec
}

type mockResourceV2Ok struct {
	called bool
}

func (m *mockResourceV2Ok) Ok(w http.ResponseWriter, _ *http.Request) error {
	m.called = true
	w.WriteHeader(http.StatusOK)
	_, _ = w.Write([]byte("ok"))
	return nil
}

func (m *mockResourceV2Ok) Auth(w http.ResponseWriter, _ *http.Request) error {
	m.called = true
	w.WriteHeader(http.StatusOK)
	_, _ = w.Write([]byte("auth"))
	return nil
}

func (m *mockResourceV2Ok) List(w http.ResponseWriter, _ *http.Request) error {
	m.called = true
	w.WriteHeader(http.StatusOK)
	_, _ = w.Write([]byte("list"))
	return nil
}

func (m *mockResourceV2Ok) Logout(w http.ResponseWriter, _ *http.Request) error {
	m.called = true
	w.WriteHeader(http.StatusOK)
	_, _ = w.Write([]byte("logout"))
	return nil
}

func (m *mockResourceV2Ok) Refresh(w http.ResponseWriter, _ *http.Request) error {
	m.called = true
	w.WriteHeader(http.StatusOK)
	_, _ = w.Write([]byte("refresh"))
	return nil
}

func (m *mockResourceV2Ok) Register(w http.ResponseWriter, _ *http.Request) error {
	m.called = true
	w.WriteHeader(http.StatusOK)
	_, _ = w.Write([]byte("register"))
	return nil
}

func (m *mockResourceV2Ok) SessionRoles(w http.ResponseWriter, _ *http.Request) error {
	m.called = true
	w.WriteHeader(http.StatusOK)
	_, _ = w.Write([]byte("session roles"))
	return nil
}
