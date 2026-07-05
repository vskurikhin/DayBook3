package model

import (
	"testing"

	"github.com/stretchr/testify/require"
)

func TestMakeCredValuesV2_AndGetters(t *testing.T) {
	tests := []struct {
		name       string
		sessionID  SessionID
		timeTokens ValidTimeTokens
		user       User
	}{
		{
			name:       "all fields filled",
			sessionID:  SessionID{},
			timeTokens: ValidTimeTokens{},
			user:       User{},
		},
		{
			name:       "zero values",
			sessionID:  SessionID{},
			timeTokens: ValidTimeTokens{},
			user:       User{},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := MakeCredValuesV2(tt.sessionID, tt.timeTokens, tt.user)

			require.Equal(t, tt.sessionID, result.SessionID())
			require.Equal(t, tt.timeTokens, result.TimeTokens())
			require.Equal(t, tt.user, result.User())
		})
	}
}
