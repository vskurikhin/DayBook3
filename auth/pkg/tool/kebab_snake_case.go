package tool

import "strings"

func KebabCaseToSnakeCase(s string) string {
	result := strings.ReplaceAll(s, "-", "_")
	return strings.ToLower(result)
}

func SnakeCaseToKebabCase(s string) string {
	result := strings.ReplaceAll(s, "_", "-")
	return strings.ToLower(result)
}
