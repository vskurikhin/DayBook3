package model

import "time"

type ValidTimeTokens struct {
	accessTokenTime   time.Time
	cookieExpiresTime time.Time
	refreshTokenTime  time.Time
	sessionValidTime  time.Time
}

func (v ValidTimeTokens) AccessTokenTime() time.Time {
	return v.accessTokenTime
}

func (v ValidTimeTokens) CookieExpiresTime() time.Time {
	return v.cookieExpiresTime
}

func (v ValidTimeTokens) RefreshTokenTime() time.Time {
	return v.refreshTokenTime
}

func (v ValidTimeTokens) SessionValidTime() time.Time {
	return v.sessionValidTime
}

func MakeValidTimeTokens(validPeriodAccessToken, validPeriodRefreshToken time.Duration) ValidTimeTokens {
	return ValidTimeTokens{
		accessTokenTime:   time.Now().Add(validPeriodAccessToken),
		cookieExpiresTime: time.Now().Add(validPeriodRefreshToken).Add(time.Second),
		refreshTokenTime:  time.Now().Add(validPeriodRefreshToken),
		sessionValidTime:  time.Now().Add(validPeriodRefreshToken),
	}
}
