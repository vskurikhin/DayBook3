package dto

import "time"

type Token struct {
	ExpiresAt time.Time `json:"expires_at"`
	JWT       string    `json:"token"`
	User      User      `json:"user"`
}
