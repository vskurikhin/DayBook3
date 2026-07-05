package handler

import (
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"github.com/go-chi/chi/v5"
	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/env"
	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

//
// ---- Mock Environments ----
//

type mockEnv struct {
	values env.Values
}

func (m mockEnv) Values() env.Values {
	return m.values
}

var environments = mockEnv{
	values: env.Values{
		Timeout: 5 * time.Second,
	},
}

func newMockEnvironments(timeout time.Duration, debugPprof bool) env.Environments {
	return &mockEnv{
		values: env.Values{
			Timeout:    timeout,
			DebugPprof: debugPprof,
		},
	}
}

// Mocks Routers

var (
	// Empty Router
	emptyRouter = func() chi.Router {
		r := chi.NewRouter()
		r.Get("/", func(w http.ResponseWriter, r *http.Request) {})
		return r
	}()
	// Ok Router
	okRouter = func() chi.Router {
		r := chi.NewRouter()
		r.Get(OkURL, func(w http.ResponseWriter, r *http.Request) {
			_, _ = w.Write([]byte("ok"))
		})
		return r
	}()
)

//
// ---- Tests ----
//

func TestNewRouter_ReturnsHandler(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()
	cfgMock := NewMockConfig(ctrl)
	cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()
	router := NewRouter(cfgMock, environments, emptyRouter, emptyRouter)

	if router == nil {
		t.Fatal("expected router, got nil")
	}
}

func TestNewRouter_CORSHeaders(t *testing.T) {
	ctrl := gomock.NewController(t)
	defer ctrl.Finish()

	cfgMock := NewMockConfig(ctrl)
	cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

	router := NewRouter(cfgMock, environments, emptyRouter, emptyRouter)

	req := httptest.NewRequest(http.MethodOptions, "/auth/api/v1/ok", nil)
	req.Header.Set("Origin", "http://localhost")
	req.Header.Set("Access-Control-Request-Method", "GET")

	rr := httptest.NewRecorder()

	router.ServeHTTP(rr, req)

	if rr.Header().Get("Access-Control-Allow-Origin") == "" {
		t.Fatal("expected CORS header, got none")
	}
}

func TestNewRouter_GetEndpoints(t *testing.T) {
	type testType struct {
		name          string
		code          int
		endpoint      string
		method        string
		newRouterFunc func(t gomock.TestReporter, opts ...gomock.ControllerOption) (http.Handler, *gomock.Controller)
		ts            tool.TestStruct
	}
	tests := []testType{
		{
			name: "positive #1 returns 200 (OK) endpoint " + BaseURL + V1,
			ts: tool.TestStruct{
				Enable:  true,
				WantErr: false,
			},
			newRouterFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (http.Handler, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				envNts := newMockEnvironments(1*time.Second, true)
				cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

				return NewRouter(cfgMock, envNts, okRouter, emptyRouter), ctrl
			},
			code:     http.StatusOK,
			endpoint: "/auth/api/v1/ok",
			method:   http.MethodGet,
		},
		{
			name: "positive #2 returns 200 (OK) endpoint " + BaseURL + V2,
			ts: tool.TestStruct{
				Enable:  true,
				WantErr: false,
			},
			newRouterFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (http.Handler, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				envNts := newMockEnvironments(1*time.Second, true)
				cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

				return NewRouter(cfgMock, envNts, emptyRouter, okRouter), ctrl
			},
			code:     http.StatusOK,
			endpoint: "/auth/api/v2/ok",
			method:   http.MethodGet,
		},
		{
			name: "positive #3 returns 200 (OK) endpoint " + DebugURL,
			ts: tool.TestStruct{
				Enable:  true,
				WantErr: false,
			},
			newRouterFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (http.Handler, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				envNts := newMockEnvironments(1*time.Second, true)
				cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

				return NewRouter(cfgMock, envNts, emptyRouter, emptyRouter), ctrl
			},
			code:     http.StatusOK,
			endpoint: "/debug",
			method:   http.MethodGet,
		},
		{
			name: "negative #4 returns 404 (NotFound) endpoint /unknown",
			ts: tool.TestStruct{
				Enable:  true,
				WantErr: false,
			},
			newRouterFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (http.Handler, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				envNts := newMockEnvironments(1*time.Second, true)
				cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

				return NewRouter(cfgMock, envNts, emptyRouter, emptyRouter), ctrl
			},
			code:     http.StatusNotFound,
			endpoint: "/unknown",
			method:   http.MethodGet,
		},
		{
			name: "negative #5 returns 405 (MethodNotAllowed) endpoint " + BaseURL + V1,
			ts: tool.TestStruct{
				Enable:  true,
				WantErr: false,
			},
			newRouterFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (http.Handler, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				envNts := newMockEnvironments(1*time.Second, true)
				cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

				return NewRouter(cfgMock, envNts, okRouter, emptyRouter), ctrl
			},
			code:     http.StatusMethodNotAllowed,
			endpoint: "/auth/api/v1/ok",
			method:   http.MethodPatch,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if tt.ts.Enable {
				router, ctrl := tt.newRouterFunc(t)
				defer ctrl.Finish()

				req := httptest.NewRequest(tt.method, tt.endpoint, nil)
				req.Host = "localhost"
				rr := httptest.NewRecorder()

				router.ServeHTTP(rr, req)

				if rr.Code != tt.code {
					t.Fatalf("expected status %d, got %d", tt.code, rr.Code)
				}

			}
		})
	}
}

func TestNewRouter_DebugPprofEndpoints_Returns200(t *testing.T) {
	type testType struct {
		name          string
		code          int
		newRouterFunc func(t gomock.TestReporter, opts ...gomock.ControllerOption) (http.Handler, *gomock.Controller)
		ts            tool.TestStruct
	}
	tests := []testType{
		{
			name: "TestNewRouter_DebugPprofEndpoints_Returns200",
			ts: tool.TestStruct{
				Enable:  true,
				WantErr: false,
			},
			newRouterFunc: func(t gomock.TestReporter, opts ...gomock.ControllerOption) (http.Handler, *gomock.Controller) {
				ctrl := gomock.NewController(t)
				cfgMock := NewMockConfig(ctrl)
				envNts := newMockEnvironments(1*time.Second, true)
				cfgMock.EXPECT().Values().Return(config.Values{}).AnyTimes()

				return NewRouter(cfgMock, envNts, emptyRouter, emptyRouter), ctrl
			},
			code: http.StatusOK,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if tt.ts.Enable {
				router, ctrl := tt.newRouterFunc(t)
				defer ctrl.Finish()

				req := httptest.NewRequest(http.MethodGet, "/debug", nil)
				req.Host = "localhost"
				rr := httptest.NewRecorder()

				router.ServeHTTP(rr, req)

				if rr.Code != tt.code {
					t.Fatalf("expected status 200, got %d", rr.Code)
				}

			}
		})
	}
}
