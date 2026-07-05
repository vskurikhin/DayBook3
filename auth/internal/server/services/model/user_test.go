package model

import (
	"bytes"
	"encoding/json"
	"testing"
	"time"

	"github.com/google/uuid"
	"github.com/jackc/pgx/v5/pgtype"
	"github.com/stretchr/testify/assert"

	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/dto"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_attrs"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_name"
	"github.com/vskurikhin/DayBook3/auth/v2/internal/server/repository/user_view"
)

func TestCreateUser_ToModelParams(t *testing.T) {
	id := uuid.New()

	u := CreateUser{
		id:       pgtype.UUID{Bytes: id, Valid: true},
		email:    "test@mail.com",
		name:     "name",
		password: "pass",
		userName: "user",
	}

	t.Run("ToModelCreateUserNameParams", func(t *testing.T) {
		got := u.ToModelCreateUserNameParams()

		if got.Password != u.password {
			t.Error("password mismatch")
		}
		if got.UserName != u.userName {
			t.Error("username mismatch")
		}
	})

	t.Run("ToModelCreateUserAttrsParams", func(t *testing.T) {
		got := u.ToModelCreateUserAttrsParams()

		if got.Name != u.name {
			t.Error("name mismatch")
		}
		if got.UserName != u.userName {
			t.Error("username mismatch")
		}

		var decoded userAttrs
		if err := json.NewDecoder(bytes.NewBuffer(got.Attrs)).Decode(&decoded); err != nil {
			t.Fatalf("failed to decode attrs: %v", err)
		}

		if decoded.Email != u.email {
			t.Error("email mismatch")
		}
		if decoded.ID != id {
			t.Error("id mismatch")
		}
	})
}

func TestCreateUserFromDto(t *testing.T) {
	dtoUser := dto.CreateUser{
		Email:           "mail",
		Name:            "name",
		Password:        "pass",
		UserName:        "user",
		ConfirmPassword: "pass",
	}

	got := CreateUserFromDto(dtoUser)

	if got.email != dtoUser.Email ||
		got.name != dtoUser.Name ||
		got.password != dtoUser.Password ||
		got.userName != dtoUser.UserName {
		t.Error("mapping failed")
	}
}

func TestCreatedUser_ToDto(t *testing.T) {
	id := uuid.New()
	now := time.Now()

	u := CreatedUser{
		id:       id,
		userName: "user",
		created:  now,
		visible:  true,
		flags:    1,
	}

	dto := u.ToDto()

	if dto.ID != id ||
		dto.UserName != "user" ||
		!dto.CreatedAt.Equal(now) ||
		dto.Visible != true ||
		dto.Flags != 1 {
		t.Error("dto mapping failed")
	}
}

func TestLogin(t *testing.T) {
	t.Run("UserNamePgTypeText", func(t *testing.T) {
		l := Login{userName: "user"}

		got := l.UserNamePgTypeText()

		if !got.Valid || got.String != "user" {
			t.Error("pgtype text incorrect")
		}
	})

	t.Run("LoginFromDto", func(t *testing.T) {
		dtoLogin := dto.Login{
			UserName: "user",
			Password: "pass",
			Visible:  true,
			Flags:    2,
		}

		got := LoginFromDto(dtoLogin)

		if got.userName != dtoLogin.UserName ||
			got.password != dtoLogin.Password ||
			got.visible != dtoLogin.Visible ||
			got.flags != dtoLogin.Flags {
			t.Error("mapping failed")
		}
	})
}

func TestUser_ToDto(t *testing.T) {
	id := uuid.New()

	u := User{
		id:       id,
		name:     "name",
		email:    "mail",
		userName: "user",
		visible:  true,
		flags:    1,
	}

	dto := u.ToDto()

	if dto.ID != id ||
		dto.Name != u.name ||
		dto.Email != u.email ||
		dto.UserName != u.userName ||
		dto.Visible != u.visible ||
		dto.Flags != u.flags {
		t.Error("dto mapping failed")
	}
}

func TestUserFromModelUserView(t *testing.T) {
	id := uuid.New()

	attrs := userAttrs{
		ID:       id,
		Email:    "mail",
		Name:     "name",
		UserName: "user",
	}

	data, _ := json.Marshal(attrs)

	input := user_view.UserView{
		ID:       pgtype.UUID{Bytes: id, Valid: true},
		Name:     pgtype.Text{String: "name", Valid: true},
		UserName: pgtype.Text{String: "user", Valid: true},
		Attrs:    data,
		Visible:  pgtype.Bool{Bool: true, Valid: true},
		Flags:    pgtype.Int4{Int32: 3, Valid: true},
	}

	got := UserFromModelUserView(input)

	if got.id != id ||
		got.email != "mail" ||
		got.name != "name" ||
		got.userName != "user" ||
		!got.visible ||
		got.flags != 3 {
		t.Error("mapping failed")
	}
}

func TestUserFromModelUserName(t *testing.T) {
	id := uuid.New()

	input := user_name.UserName{
		ID:       pgtype.UUID{Bytes: id, Valid: true},
		UserName: "user",
		Visible:  true,
		Flags:    5,
	}

	got := UserFromModelUserName(input)

	if got.id != id ||
		got.userName != "user" ||
		got.visible != true ||
		got.flags != 5 {
		t.Error("mapping failed")
	}
}

func TestUserFromModelUserAttr(t *testing.T) {
	id := uuid.New()

	attrs := userAttrs{
		ID:       id,
		Email:    "mail",
		Name:     "name",
		UserName: "user",
	}

	data, _ := json.Marshal(attrs)

	input := user_attrs.UserAttr{
		Name:     "name",
		UserName: "user",
		Attrs:    data,
		Visible:  true,
		Flags:    7,
	}

	got, err := UserFromModelUserAttr(input)
	assert.Nil(t, err)

	if got.id != id ||
		got.email != "mail" ||
		got.name != "name" ||
		got.userName != "user" ||
		got.visible != true ||
		got.flags != 7 {
		t.Error("mapping failed")
	}
}
