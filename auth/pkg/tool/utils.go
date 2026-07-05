package tool

import "github.com/google/uuid"

func Map[T any, U any](input []T, fn func(T) U) []U {
	output := make([]U, len(input))
	for i, v := range input {
		output[i] = fn(v)
	}
	return output
}

func UUIDToSlice(u uuid.UUID) []byte {
	return u[:]
}
