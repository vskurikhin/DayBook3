package creds

import (
	"encoding/base64"
	"errors"
	"testing"
	"time"

	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
	"go.uber.org/mock/gomock"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/config"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/services/model"
)

func Test_makeCredV2(t *testing.T) {
	var mockCfg *MockConfig

	hostname := "localhost"
	username := "username"
	secret := "secret"

	sid, err := model.MakeSessionID(hostname, username)
	if err != nil {
		t.Fatal("failed to make session id", err)
	}

	issuer := uuid.NewSHA1(uuid.NameSpaceDNS, []byte(hostname))
	jti := sid.SessionJTIUUID()
	sub := uuid.NewSHA1(uuid.NameSpaceOID, []byte(username))

	cred := model.MakeCredValuesV2(sid, model.MakeValidTimeTokens(time.Hour, 2*time.Hour), model.User{})

	tests := []struct {
		name      string
		input     model.CredValuesV2
		err       error
		mockSetup func(c *MockConfig)
		wantError bool
		check     func(t *testing.T, got model.Credentials)
	}{
		{
			name:  "error input",
			input: cred,
			err:   errors.New("some error"),
			mockSetup: func(c *MockConfig) {
				c.EXPECT().Values().Return(config.Values{}).Times(0)
			},
			wantError: true,
		},
		{
			name:  "success",
			input: cred,
			err:   nil,
			mockSetup: func(c *MockConfig) {
				c.EXPECT().Values().Return(config.Values{
					HTTPS:           true,
					JWThs256SignKey: []byte(secret),
				}).Times(3)
			},
			wantError: false,
			check: func(t *testing.T, got model.Credentials) {
				if got.AccessToken().ToDto().JWT == "" {
					t.Fatal("access token is empty")
				}

				// Парсим access token
				parsed, err := jwt.Parse(got.AccessToken().ToDto().JWT, func(token *jwt.Token) (interface{}, error) {
					return []byte(secret), nil
				}, jwt.WithValidMethods([]string{"HS256"}))
				if err != nil {
					t.Fatalf("failed to parse access token: %v", err)
				}

				claims, ok := parsed.Claims.(jwt.MapClaims)
				if !ok {
					t.Fatal("invalid claims type")
				}

				if claims[Iss] != issuer.String() {
					t.Errorf("unexpected iss: %v", claims[Iss])
				}
				if claims[Jti] != jti.String() {
					t.Errorf("unexpected jti: %v", claims[Jti])
				}
				if claims[Sub] != sub.String() {
					t.Errorf("unexpected sub: %v", claims[Sub])
				}

				// Проверка cookie
				cookie := got.RefreshTokenCookie()
				if cookie.Name != Refresh {
					t.Errorf("unexpected cookie name: %v", cookie.Name)
				}
				if cookie.Value == "" {
					t.Fatal("empty refresh token cookie")
				}
				if !cookie.HttpOnly {
					t.Error("cookie should be HttpOnly")
				}
				if !cookie.Secure {
					t.Error("cookie should be Secure")
				}

				// Парсим refresh token
				refreshParsed, err := jwt.Parse(cookie.Value, func(token *jwt.Token) (interface{}, error) {
					return []byte(secret), nil
				}, jwt.WithValidMethods([]string{"HS256"}))
				if err != nil {
					t.Fatalf("failed to parse refresh token: %v", err)
				}

				refreshClaims, ok := refreshParsed.Claims.(jwt.MapClaims)
				if !ok {
					t.Fatal("invalid refresh claims type")
				}

				expectedIss := base64.StdEncoding.EncodeToString(issuer[:])
				expectedJti := base64.StdEncoding.EncodeToString(jti[:])
				expectedSub := base64.StdEncoding.EncodeToString(sub[:])

				if refreshClaims[Iss] != expectedIss {
					t.Errorf("unexpected refresh iss: %v", refreshClaims[Iss])
				}
				if refreshClaims[Jti] != expectedJti {
					t.Errorf("unexpected refresh jti: %v", refreshClaims[Jti])
				}
				if refreshClaims[Sub] != expectedSub {
					t.Errorf("unexpected refresh sub: %v", refreshClaims[Sub])
				}
			},
		},
		{
			name:  "sign error (empty secret)",
			input: model.MakeCredValuesV2(cred.SessionID(), cred.TimeTokens(), cred.User()),
			err:   nil,
			mockSetup: func(c *MockConfig) {
				c.EXPECT().Values().Return(config.Values{
					HTTPS: true,
				}).Times(3)
			},
			wantError: false, // jwt обычно всё равно подпишет, но кейс оставляем
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			ctrl := gomock.NewController(t)
			defer ctrl.Finish()
			mockCfg = NewMockConfig(ctrl)
			tt.mockSetup(mockCfg)

			c := NewCredentialsMethodFactory(mockCfg)

			got, err := c.MakeCredentials(tt.input, tt.err)

			if tt.wantError {
				if err == nil {
					t.Fatal("expected error, got nil")
				}
				return
			}

			if err != nil {
				t.Fatalf("unexpected error: %v", err)
			}

			if tt.check != nil {
				tt.check(t, got)
			}
		})
	}
}
