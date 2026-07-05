package tool

import (
	"strconv"
	"testing"
)

func TestMap(t *testing.T) {
	tests := []struct {
		name     string
		input    []int
		fn       func(int) string
		expected []string
	}{
		{
			name:     "empty slice",
			input:    []int{},
			fn:       func(i int) string { return strconv.Itoa(i) },
			expected: []string{},
		},
		{
			name:  "int to string",
			input: []int{1, 2, 3},
			fn:    func(i int) string { return strconv.Itoa(i) },
			expected: []string{
				"1", "2", "3",
			},
		},
		{
			name:  "square numbers",
			input: []int{1, 2, 3},
			fn:    func(i int) string { return strconv.Itoa(i * i) },
			expected: []string{
				"1", "4", "9",
			},
		},
		{
			name:  "negative numbers",
			input: []int{-1, -2},
			fn:    func(i int) string { return strconv.Itoa(i) },
			expected: []string{
				"-1", "-2",
			},
		},
		{
			name:  "zero values",
			input: []int{0, 0},
			fn:    func(i int) string { return strconv.Itoa(i) },
			expected: []string{
				"0", "0",
			},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := Map(tt.input, tt.fn)

			if len(result) != len(tt.expected) {
				t.Fatalf("expected length %d, got %d", len(tt.expected), len(result))
			}

			for i := range result {
				if result[i] != tt.expected[i] {
					t.Fatalf("at index %d: expected %s, got %s", i, tt.expected[i], result[i])
				}
			}
		})
	}
}
