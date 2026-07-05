package tool

import (
	"testing"

	"github.com/stretchr/testify/require"
)

func TestIsReallyNil(t *testing.T) {
	type testStruct struct{}

	var (
		nilPtr       *testStruct
		nilSlice     []int
		nilMap       map[string]int
		nilChan      chan int
		nilFunc      func()
		nilInterface interface{}
	)

	tests := []struct {
		name  string
		input interface{}
		want  bool
	}{
		{
			name:  "nil interface",
			input: nil,
			want:  true,
		},
		{
			name:  "typed nil pointer",
			input: nilPtr,
			want:  true,
		},
		{
			name:  "typed nil slice",
			input: nilSlice,
			want:  true,
		},
		{
			name:  "typed nil map",
			input: nilMap,
			want:  true,
		},
		{
			name:  "typed nil chan",
			input: nilChan,
			want:  true,
		},
		{
			name:  "typed nil func",
			input: nilFunc,
			want:  true,
		},
		{
			name:  "nil interface value",
			input: nilInterface,
			want:  true,
		},
		{
			name:  "non-nil pointer",
			input: &testStruct{},
			want:  false,
		},
		{
			name:  "empty but non-nil slice",
			input: make([]int, 0),
			want:  false,
		},
		{
			name:  "empty but non-nil map",
			input: make(map[string]int),
			want:  false,
		},
		{
			name:  "non-nil value type",
			input: 123,
			want:  false,
		},
		{
			name:  "non-nil struct",
			input: testStruct{},
			want:  false,
		},
		{
			name:  "interface with non-nil value",
			input: interface{}(123),
			want:  false,
		},
		{
			name:  "interface with typed nil pointer",
			input: interface{}(nilPtr),
			want:  true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := IsReallyNil(tt.input)
			require.Equal(t, tt.want, got)
		})
	}
}
