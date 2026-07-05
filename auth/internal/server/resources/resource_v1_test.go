package resources

import (
	"net/http"
	"net/http/httptest"
	"testing"

	"go.uber.org/mock/gomock"
)

func TestV1_Ok(t *testing.T) {
	tests := []struct {
		name           string
		mockReturn     string
		expectedStatus int
		expectedBody   string
	}{
		{
			name:           "success simple response",
			mockReturn:     "ok",
			expectedStatus: http.StatusOK,
			expectedBody:   "ok",
		},
		{
			name:           "empty response",
			mockReturn:     "",
			expectedStatus: http.StatusOK,
			expectedBody:   "",
		},
		{
			name:           "custom message",
			mockReturn:     "hello world",
			expectedStatus: http.StatusOK,
			expectedBody:   "hello world",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()

			mockService := NewMockOkServiceV1(ctrl)
			mockService.EXPECT().
				Ok().
				Return(tt.mockReturn).
				Times(1)

			resource := NewV1(mockService)

			req := httptest.NewRequest(http.MethodGet, "/v1/ok", nil)
			rec := httptest.NewRecorder()

			resource.Ok(rec, req)

			if rec.Code != tt.expectedStatus {
				t.Fatalf("expected status %d, got %d", tt.expectedStatus, rec.Code)
			}

			if rec.Body.String() != tt.expectedBody {
				t.Fatalf("expected body %q, got %q", tt.expectedBody, rec.Body.String())
			}
		})
	}
}
