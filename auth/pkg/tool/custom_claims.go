package tool

import (
	"github.com/golang-jwt/jwt/v5"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/xerror"
)

type CustomClaims struct {
	Jti string `json:"jti"` // Map the "jti" claim to a Go field
	jwt.RegisteredClaims
}

func ExtractJTI(c jwt.Claims) (string, error) {
	if claims, ok := c.(*CustomClaims); ok {
		return claims.Jti, nil
	}
	return "", xerror.ErrInvalidTokenClaims
}
