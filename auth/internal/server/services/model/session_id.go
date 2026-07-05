package model

import (
	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
	"github.com/jackc/pgx/v5/pgtype"
	jwx "github.com/lestrrat-go/jwx/v3/jwt"

	"github.com/vskurikhin/DayBook3/auth/v2/pkg/tool"
)

type SessionPrimaryKey struct {
	Iss pgtype.UUID
	Jti pgtype.UUID
	Sub pgtype.UUID
}

type SessionID struct {
	issuerUUID     uuid.UUID
	userNameUUID   uuid.UUID
	sessionJTIUUID uuid.UUID
}

func (s SessionID) IssuerUUID() uuid.UUID {
	return s.issuerUUID
}

func (s SessionID) UserNameUUID() uuid.UUID {
	return s.userNameUUID
}

func (s SessionID) SessionJTIUUID() uuid.UUID {
	return s.sessionJTIUUID
}

func MakeSessionID(hostname, UserName string) (SessionID, error) {
	sessionJTIUUID, err := uuid.NewV7()
	if err != nil {
		return SessionID{}, err
	}
	return SessionID{
		issuerUUID:     uuid.NewSHA1(uuid.NameSpaceDNS, []byte(hostname)),
		userNameUUID:   uuid.NewSHA1(uuid.NameSpaceOID, []byte(UserName)),
		sessionJTIUUID: sessionJTIUUID,
	}, nil
}

func SessionIDFromClaims(claims jwt.Claims) (SessionID, error) {
	issuerUUID, errDecodeIssuer := tool.CanClaimUUID(claims.GetIssuer())
	if errDecodeIssuer != nil {
		return SessionID{}, errDecodeIssuer
	}
	userNameUUID, errDecodeSubject := tool.CanClaimUUID(claims.GetSubject())
	if errDecodeSubject != nil {
		return SessionID{}, errDecodeSubject
	}
	sessionJTIUUID, errDecodeJTI := tool.CanClaimUUID(tool.ExtractJTI(claims))
	if errDecodeJTI != nil {
		return SessionID{}, errDecodeJTI
	}
	return SessionID{
		issuerUUID:     issuerUUID,
		userNameUUID:   userNameUUID,
		sessionJTIUUID: sessionJTIUUID,
	}, nil
}

func SessionIDFromJwxToken(bearerToken jwx.Token) (SessionID, error) {
	issuerUUID, errDecodeIssuer := tool.CanUUIDParse(tool.JwxTokenIssuer(bearerToken))
	if errDecodeIssuer != nil {
		return SessionID{}, errDecodeIssuer
	}
	userNameUUID, errDecodeSubject := tool.CanUUIDParse(tool.JwxTokenSubject(bearerToken))
	if errDecodeSubject != nil {
		return SessionID{}, errDecodeSubject
	}
	sessionJTIUUID, errDecodeJTI := tool.CanUUIDParse(tool.JwxTokenJTI(bearerToken))
	if errDecodeJTI != nil {
		return SessionID{}, errDecodeJTI
	}
	return SessionID{
		issuerUUID:     issuerUUID,
		userNameUUID:   userNameUUID,
		sessionJTIUUID: sessionJTIUUID,
	}, nil
}

func (s SessionID) ToModelPrimaryKey() SessionPrimaryKey {
	return SessionPrimaryKey{
		Iss: pgtype.UUID{Bytes: s.issuerUUID, Valid: true},
		Jti: pgtype.UUID{Bytes: s.sessionJTIUUID, Valid: true},
		Sub: pgtype.UUID{Bytes: s.userNameUUID, Valid: true},
	}
}
