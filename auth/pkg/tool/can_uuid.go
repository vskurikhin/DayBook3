package tool

import (
	"encoding/base64"

	"github.com/google/uuid"
)

func CanClaimUUID(s string, err error) (uuid.UUID, error) {
	if err != nil {
		return uuid.Nil, err
	}
	buf, errDecode := base64.StdEncoding.DecodeString(s)
	if errDecode != nil {
		return uuid.Nil, errDecode
	}
	var array [16]byte
	copy(array[:], buf)
	return array, nil
}

func CanUUIDParse(s string, err error) (uuid.UUID, error) {
	if err != nil {
		return uuid.Nil, err
	}
	return uuid.Parse(s)
}
