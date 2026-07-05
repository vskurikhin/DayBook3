// Package resources defines the resource layer for the API server.
//
// It provides implementations for API versioned resources (V1, V2),
// exposing handlers that can be registered with HTTP routers.
//
// Each resource type implements a corresponding interface (ResourceV1, ResourceV2),
// allowing for easy mocking and unit testing of the API endpoints.
//
// Typical usage:
//
//	// Create a new V1 resource
//	v1 := resources.NewV1()
//
//	// Register the handler in router
//	router.Get("/ok", v1.Ok)
//
// Environment and context handling:
//
//   - Each handler receives an http.Request and http.ResponseWriter.
//   - The Ok handlers respect the request context, returning early if canceled.
package resources
