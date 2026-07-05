package dto

import (
	"time"

	"github.com/google/uuid"
)

type CreateUser struct {
	Name            string `json:"name"`
	UserName        string `json:"user_name"`
	Email           string `json:"email"`
	Password        string `json:"password"`
	ConfirmPassword string `json:"confirm_password"`
}

type CreatedUser struct {
	ID        uuid.UUID `json:"id"`
	UserName  string    `json:"user_name"`
	CreatedAt time.Time `json:"created_at"`
	Visible   bool      `json:"visible,omitempty" swaggerignore:"true"`
	Flags     int32     `json:"flags,omitempty"  swaggerignore:"true"`
}

type Login struct {
	UserName string `json:"user_name"`
	Password string `json:"password"`
	Visible  bool   `json:"visible,omitempty" swaggerignore:"true"`
	Flags    int32  `json:"flags,omitempty"  swaggerignore:"true"`
}

type User struct {
	ID       uuid.UUID `json:"id"`
	Name     string    `json:"name"`
	Email    string    `json:"email"`
	UserName string    `json:"user_name"`
	Visible  bool      `json:"visible"`
	Flags    int32     `json:"flags"`
}

type UserHasRoles struct {
	UserName string   `json:"user_name"`
	Roles    []string `json:"roles"`
	Visible  bool     `json:"visible"`
	Flags    int32    `json:"flags"`
}
