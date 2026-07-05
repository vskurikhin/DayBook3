package services

import (
	"errors"

	"github.com/google/uuid"
	"github.com/jackc/pgx/v5/pgtype"
	jwx "github.com/lestrrat-go/jwx/v3/jwt"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/session"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_attrs"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_has_roles"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_name"
)

func newTestJWXToken() jwx.Token {
	token := jwx.New()
	_ = token.Set(jwx.IssuerKey, uuid.New().String())
	_ = token.Set(jwx.SubjectKey, uuid.New().String())
	_ = token.Set(jwx.JwtIDKey, uuid.New().String())
	return token
}

type mockRowSession struct {
	data session.Session
}

func (s *mockRowSession) Scan(dest ...any) error {

	*(dest[0].(*pgtype.UUID)) = s.data.Iss
	*(dest[1].(*pgtype.UUID)) = s.data.Jti
	*(dest[2].(*pgtype.UUID)) = s.data.Sub

	*(dest[3].(*string)) = s.data.UserName
	*(dest[4].(*[]string)) = s.data.Roles

	*(dest[5].(*pgtype.Timestamptz)) = s.data.ValidTime
	*(dest[6].(*pgtype.Timestamp)) = s.data.CreateTime
	*(dest[7].(*pgtype.Timestamp)) = s.data.UpdateTime

	*(dest[8].(*bool)) = s.data.Enabled
	*(dest[9].(*bool)) = s.data.LocalChange
	*(dest[10].(*pgtype.Bool)) = s.data.Visible
	*(dest[11].(*int32)) = s.data.Flags

	return nil
}

type mockRowSessionError struct {
}

func (m mockRowSessionError) Scan(...any) error {
	return errors.New("error")
}

type mockRowUserAttrs struct {
	data user_attrs.UserAttr
}

func (s *mockRowUserAttrs) Scan(dest ...any) error {

	*(dest[0].(*string)) = s.data.UserName
	*(dest[1].(*[]byte)) = s.data.Attrs
	*(dest[2].(*string)) = s.data.Name

	*(dest[3].(*pgtype.Timestamp)) = s.data.CreateTime
	*(dest[4].(*pgtype.Timestamp)) = s.data.UpdateTime

	*(dest[5].(*bool)) = s.data.Enabled
	*(dest[6].(*bool)) = s.data.LocalChange
	*(dest[7].(*bool)) = s.data.Visible
	*(dest[8].(*int32)) = s.data.Flags

	return nil
}

type mockRowUserHasRoles struct {
	data user_has_roles.GetRolesForUserNameRow
}

func (s *mockRowUserHasRoles) Scan(dest ...any) error {

	*(dest[0].(*string)) = s.data.UserName
	*(dest[1].(*bool)) = s.data.Enabled
	*(dest[2].(*[]string)) = s.data.Roles

	return nil
}

type mockRowUserName struct {
	data user_name.UserName
}

func (s *mockRowUserName) Scan(dest ...any) error {

	*(dest[0].(*string)) = s.data.UserName
	*(dest[1].(*pgtype.UUID)) = s.data.ID
	*(dest[2].(*string)) = s.data.Password

	*(dest[3].(*pgtype.Timestamp)) = s.data.CreateTime
	*(dest[4].(*pgtype.Timestamp)) = s.data.UpdateTime

	*(dest[5].(*bool)) = s.data.Enabled
	*(dest[6].(*bool)) = s.data.LocalChange
	*(dest[7].(*bool)) = s.data.Visible
	*(dest[8].(*int32)) = s.data.Flags

	return nil
}

type mockRowUserNameError struct {
}

func (m mockRowUserNameError) Scan(...any) error {
	return errors.New("error")
}
