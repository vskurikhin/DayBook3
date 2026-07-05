package tool

import "testing"

func TestKebabCaseToSnakeCase(t *testing.T) {
	tests := []struct {
		input    string
		expected string
	}{
		{"simple-test", "simple_test"},
		{"multi-part-string", "multi_part_string"},
		{"AlreadySnake", "alreadysnake"},
		{"MIXED-case-Test", "mixed_case_test"},
		{"no-change", "no_change"},
		{"", ""},
	}

	for _, tt := range tests {
		got := KebabCaseToSnakeCase(tt.input)
		if got != tt.expected {
			t.Errorf("KebabCaseToSnakeCase(%q) = %q; want %q", tt.input, got, tt.expected)
		}
	}
}

func TestSnakeCaseToKebabCase(t *testing.T) {
	tests := []struct {
		input    string
		expected string
	}{
		{"simple_test", "simple-test"},
		{"multi_part_string", "multi-part-string"},
		{"AlreadyKebab", "alreadykebab"},
		{"MIXED_case_Test", "mixed-case-test"},
		{"no_change", "no-change"},
		{"", ""},
	}

	for _, tt := range tests {
		got := SnakeCaseToKebabCase(tt.input)
		if got != tt.expected {
			t.Errorf("SnakeCaseToKebabCase(%q) = %q; want %q", tt.input, got, tt.expected)
		}
	}
}
