package tool

import (
	"errors"

	"github.com/lestrrat-go/jwx/v3/jwt"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
)

func JwxTokenSubject(bearerToken jwt.Token) (string, error) {
	if bearerToken == nil {
		return "", errors.New(xerror.InvalidToken)
	}
	sub, ok := bearerToken.Subject()
	if !ok {
		return "", errors.New(xerror.InvalidToken)
	}
	return sub, nil
}

func JwxTokenJTI(bearerToken jwt.Token) (string, error) {
	if bearerToken == nil {
		return "", errors.New(xerror.InvalidToken)
	}
	jti, ok := bearerToken.JwtID()
	if !ok {
		return "", errors.New(xerror.InvalidToken)
	}
	return jti, nil
}

func JwxTokenIssuer(bearerToken jwt.Token) (string, error) {
	if bearerToken == nil {
		return "", errors.New(xerror.InvalidToken)
	}
	iss, ok := bearerToken.Issuer()
	if !ok {
		return "", errors.New(xerror.InvalidToken)
	}
	return iss, nil
}
