package model

import (
	"testing"
	"time"

	"github.com/stretchr/testify/require"
)

func TestMakeValidTimeTokens(t *testing.T) {
	tests := []struct {
		name            string
		accessDuration  time.Duration
		refreshDuration time.Duration
	}{
		{
			name:            "short durations",
			accessDuration:  time.Minute,
			refreshDuration: time.Hour,
		},
		{
			name:            "zero durations",
			accessDuration:  0,
			refreshDuration: 0,
		},
		{
			name:            "long durations",
			accessDuration:  24 * time.Hour,
			refreshDuration: 7 * 24 * time.Hour,
		},
	}

	const delta = 50 * time.Millisecond

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			before := time.Now()

			result := MakeValidTimeTokens(tt.accessDuration, tt.refreshDuration)

			after := time.Now()

			// accessTokenTime
			expectedAccessMin := before.Add(tt.accessDuration)
			expectedAccessMax := after.Add(tt.accessDuration)
			require.WithinDuration(t, expectedAccessMin, result.accessTokenTime, delta)
			require.WithinDuration(t, expectedAccessMax, result.accessTokenTime, delta)

			// refreshTokenTime
			expectedRefreshMin := before.Add(tt.refreshDuration)
			expectedRefreshMax := after.Add(tt.refreshDuration)
			require.WithinDuration(t, expectedRefreshMin, result.refreshTokenTime, delta)
			require.WithinDuration(t, expectedRefreshMax, result.refreshTokenTime, delta)

			// sessionValidTime (должен совпадать с refreshTokenTime)
			require.WithinDuration(t, result.refreshTokenTime, result.sessionValidTime, delta)

			// cookieExpiresTime = refresh + 1s
			expectedCookieMin := before.Add(tt.refreshDuration).Add(time.Second)
			expectedCookieMax := after.Add(tt.refreshDuration).Add(time.Second)
			require.WithinDuration(t, expectedCookieMin, result.cookieExpiresTime, delta)
			require.WithinDuration(t, expectedCookieMax, result.cookieExpiresTime, delta)
		})
	}
}
