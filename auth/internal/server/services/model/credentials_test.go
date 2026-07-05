package model

import (
	"net/http"
	"testing"
	"time"

	"github.com/stretchr/testify/require"
)

func TestMakeToken(t *testing.T) {
	now := time.Now()

	tests := []struct {
		name      string
		expiresAt time.Time
		jwt       string
		user      User
	}{
		{
			name:      "valid token",
			expiresAt: now,
			jwt:       "jwt-token",
			user:      User{},
		},
		{
			name:      "zero values",
			expiresAt: time.Time{},
			jwt:       "",
			user:      User{},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			token := MakeToken(tt.expiresAt, tt.jwt, tt.user)

			require.Equal(t, tt.expiresAt, token.expiresAt)
			require.Equal(t, tt.jwt, token.jwt)
			require.Equal(t, tt.user, token.user)
		})
	}
}

func TestMakeCredentials_AndGetters(t *testing.T) {
	token := Token{
		expiresAt: time.Now(),
		jwt:       "jwt",
		user:      User{},
	}

	cookie := http.Cookie{
		Name:  "refresh",
		Value: "token",
	}

	tests := []struct {
		name   string
		token  Token
		cookie http.Cookie
	}{
		{
			name:   "valid credentials",
			token:  token,
			cookie: cookie,
		},
		{
			name:   "zero values",
			token:  Token{},
			cookie: http.Cookie{},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			creds := MakeCredentials(tt.token, tt.cookie)

			require.Equal(t, tt.token, creds.AccessToken())
			require.Equal(t, tt.cookie, creds.RefreshTokenCookie())
		})
	}
}

func TestToken_ToDto(t *testing.T) {
	now := time.Now()

	tests := []struct {
		name  string
		token Token
	}{
		{
			name: "valid token",
			token: Token{
				expiresAt: now,
				jwt:       "jwt-token",
				user:      User{},
			},
		},
		{
			name:  "zero token",
			token: Token{},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			dto := tt.token.ToDto()

			require.Equal(t, tt.token.expiresAt, dto.ExpiresAt)
			require.Equal(t, tt.token.jwt, dto.JWT)
			require.Equal(t, tt.token.user.ToDto(), dto.User)
		})
	}
}
