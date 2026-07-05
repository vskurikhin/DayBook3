package model

import (
	"encoding/base64"
	"testing"

	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
	jwx "github.com/lestrrat-go/jwx/v3/jwt"
	"github.com/stretchr/testify/require"

	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

func TestMakeSessionID(t *testing.T) {
	tests := []struct {
		name     string
		hostname string
		username string
	}{
		{
			name:     "valid input",
			hostname: "example.com",
			username: "user1",
		},
		{
			name:     "empty values",
			hostname: "",
			username: "",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			sid, err := MakeSessionID(tt.hostname, tt.username)

			require.NoError(t, err)
			require.NotEqual(t, uuid.Nil, sid.issuerUUID)
			require.NotEqual(t, uuid.Nil, sid.userNameUUID)
			require.NotEqual(t, uuid.Nil, sid.sessionJTIUUID)

			// deterministic SHA1
			expectedIssuer := uuid.NewSHA1(uuid.NameSpaceDNS, []byte(tt.hostname))
			expectedUser := uuid.NewSHA1(uuid.NameSpaceOID, []byte(tt.username))

			require.Equal(t, expectedIssuer, sid.issuerUUID)
			require.Equal(t, expectedUser, sid.userNameUUID)
		})
	}
}

func TestSessionID_Getters(t *testing.T) {
	sid := SessionID{
		issuerUUID:     uuid.New(),
		userNameUUID:   uuid.New(),
		sessionJTIUUID: uuid.New(),
	}

	require.Equal(t, sid.issuerUUID, sid.IssuerUUID())
	require.Equal(t, sid.userNameUUID, sid.UserNameUUID())
	require.Equal(t, sid.sessionJTIUUID, sid.SessionJTIUUID())
}

func TestSessionID_ToModelPrimaryKey(t *testing.T) {
	sid := SessionID{
		issuerUUID:     uuid.New(),
		userNameUUID:   uuid.New(),
		sessionJTIUUID: uuid.New(),
	}

	pk := sid.ToModelPrimaryKey()

	require.True(t, pk.Iss.Valid)
	require.True(t, pk.Jti.Valid)
	require.True(t, pk.Sub.Valid)

	require.Equal(t, sid.issuerUUID, uuid.UUID(pk.Iss.Bytes))
	require.Equal(t, sid.sessionJTIUUID, uuid.UUID(pk.Jti.Bytes))
	require.Equal(t, sid.userNameUUID, uuid.UUID(pk.Sub.Bytes))
}

func TestSessionIDFromClaims(t *testing.T) {
	validIssuer := uuid.New()
	validSubject := uuid.New()
	validJTI := uuid.New()

	tests := []struct {
		name    string
		claims  *tool.CustomClaims
		wantErr bool
	}{
		{
			name: "valid claims",
			claims: &tool.CustomClaims{
				Jti: base64.StdEncoding.EncodeToString(validJTI[:]),
				RegisteredClaims: jwt.RegisteredClaims{
					Issuer:  base64.StdEncoding.EncodeToString(validIssuer[:]),
					Subject: base64.StdEncoding.EncodeToString(validSubject[:]),
				},
			},
			wantErr: false,
		},
		{
			name: "invalid issuer",
			claims: &tool.CustomClaims{
				Jti: base64.StdEncoding.EncodeToString(validJTI[:]),
				RegisteredClaims: jwt.RegisteredClaims{
					Issuer:  "invalid",
					Subject: base64.StdEncoding.EncodeToString(validSubject[:]),
				},
			},
			wantErr: true,
		},
		{
			name: "invalid subject",
			claims: &tool.CustomClaims{
				Jti: base64.StdEncoding.EncodeToString(validJTI[:]),
				RegisteredClaims: jwt.RegisteredClaims{
					Issuer:  base64.StdEncoding.EncodeToString(validIssuer[:]),
					Subject: "invalid",
				},
			},
			wantErr: true,
		},
		{
			name: "invalid jti",
			claims: &tool.CustomClaims{
				Jti: "invalid",
				RegisteredClaims: jwt.RegisteredClaims{
					Issuer:  base64.StdEncoding.EncodeToString(validIssuer[:]),
					Subject: base64.StdEncoding.EncodeToString(validSubject[:]),
				},
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			sid, err := SessionIDFromClaims(tt.claims)

			if tt.wantErr {
				require.Error(t, err)
				return
			}

			require.NoError(t, err)
			require.Equal(t, validIssuer, sid.issuerUUID)
			require.Equal(t, validSubject, sid.userNameUUID)
			require.Equal(t, validJTI, sid.sessionJTIUUID)
		})
	}
}

func TestSessionIDFromJwxToken(t *testing.T) {
	validIssuer := uuid.New()
	validSubject := uuid.New()
	validJTI := uuid.New()

	tests := []struct {
		name    string
		setup   func() jwx.Token
		wantErr bool
	}{
		{
			name: "valid token",
			setup: func() jwx.Token {
				tok := jwx.New()
				_ = tok.Set("iss", validIssuer.String())
				_ = tok.Set("sub", validSubject.String())
				_ = tok.Set("jti", validJTI.String())
				return tok
			},
			wantErr: false,
		},
		{
			name: "invalid issuer",
			setup: func() jwx.Token {
				tok := jwx.New()
				_ = tok.Set("iss", "invalid")
				_ = tok.Set("sub", validSubject.String())
				_ = tok.Set("jti", validJTI.String())
				return tok
			},
			wantErr: true,
		},
		{
			name: "invalid subject",
			setup: func() jwx.Token {
				tok := jwx.New()
				_ = tok.Set("iss", validIssuer.String())
				_ = tok.Set("sub", "invalid")
				_ = tok.Set("jti", validJTI.String())
				return tok
			},
			wantErr: true,
		},
		{
			name: "invalid jti",
			setup: func() jwx.Token {
				tok := jwx.New()
				_ = tok.Set("iss", validIssuer.String())
				_ = tok.Set("sub", validSubject.String())
				_ = tok.Set("jti", "invalid")
				return tok
			},
			wantErr: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			token := tt.setup()

			sid, err := SessionIDFromJwxToken(token)

			if tt.wantErr {
				require.Error(t, err)
				return
			}

			require.NoError(t, err)
			require.Equal(t, validIssuer, sid.issuerUUID)
			require.Equal(t, validSubject, sid.userNameUUID)
			require.Equal(t, validJTI, sid.sessionJTIUUID)
		})
	}
}
