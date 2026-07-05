package resources

// APIResponse is a standard API response structure
type APIResponse struct {
	Success bool        `json:"success"`
	Data    interface{} `json:"data,omitempty" swaggerignore:"true"`
	Error   string      `json:"error,omitempty" swaggerignore:"true"`
}
