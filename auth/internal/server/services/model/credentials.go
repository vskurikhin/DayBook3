package model

import (
	"net/http"
	"time"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/dto"
)

type Token struct {
	expiresAt time.Time
	jwt       string
	user      User
}

func MakeToken(expiresAt time.Time, jwt string, user User) Token {
	return Token{expiresAt: expiresAt, jwt: jwt, user: user}
}

type Credentials struct {
	accessToken        Token
	refreshTokenCookie http.Cookie
}

func MakeCredentials(accessToken Token, refreshTokenCookie http.Cookie) Credentials {
	return Credentials{accessToken: accessToken, refreshTokenCookie: refreshTokenCookie}
}

func (c Credentials) AccessToken() Token {
	return c.accessToken
}

func (c Credentials) RefreshTokenCookie() http.Cookie {
	return c.refreshTokenCookie
}

func (t Token) ToDto() dto.Token {
	return dto.Token{
		ExpiresAt: t.expiresAt,
		JWT:       t.jwt,
		User:      t.user.ToDto(),
	}
}
