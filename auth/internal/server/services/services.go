// Package services provides the core business logic for authentication,
// authorization, and user/session management.
//
// It acts as an orchestration layer between transport (HTTP/handlers)
// and persistence (repositories), encapsulating all domain-specific
// workflows such as login, registration, token generation, session
// lifecycle, token refresh, logout, and user roles resolution.
//
// # Overview
// The package implements multiple versions of authentication services,
// with AuthServiceImplV2 extending the functionality of OkServiceImplV1.
// In addition, it provides specialized services such as:
//
//   - SessionRoles service: resolves user roles from an active session
//   - Refresh service: validates refresh tokens and issues new credentials
//   - Register service: handles user registration and initial session creation
//   - Logout service: terminates active sessions
//
// The services coordinate operations across several repositories:
//
//   - Session repository: manages session persistence and lifecycle
//   - User attributes repository: stores user profile data
//   - User name repository: handles identity creation
//   - User roles repository: resolves roles assigned to users
//   - User view repository: provides aggregated read models for authentication
//
// All operations are designed to be context-aware and support cancellation,
// deadlines, and structured logging.
//
// # Authentication Flow
//
// The authentication process typically consists of:
//
//  1. Fetching user credentials from the user view repository
//  2. Validating password hashes
//  3. Creating a new session with a unique session identifier
//  4. Fetching user roles
//  5. Generating access and refresh tokens (JWT-based)
//  6. Returning credentials including tokens and metadata
//
// Passwords are hashed and verified using helper utilities from the tool package.
//
// # Registration Flow
//
// User registration includes:
//
//  1. Hashing the provided password
//  2. Creating user identity and attributes
//  3. Opening a transaction
//  4. Creating an initial session
//  5. Generating credentials (access and refresh tokens)
//
// All steps are executed atomically to ensure consistency.
//
// # Token Management
//
// The package uses JWT (HS256) for both access and refresh tokens.
//
//   - Access token:
//     Contains standard claims (exp, Iss, Jti, Sub)
//     Encodes identifiers as plain UUID strings
//
//   - Refresh token:
//     Contains the same claims, but values may be encoded
//     Used to obtain new credentials without re-authentication
//
// Token validity periods are configurable and enforced during refresh.
//
// The Refresh service:
//
//   - Parses and validates the refresh token
//   - Verifies signature and claims
//   - Issues new credentials if the session is valid
//
// # Session Lifecycle
//
// Sessions are persisted in the database and identified by a composite key
// derived from:
//
//   - Issuer (Iss)
//   - Token ID (Jti)
//   - Subject (Sub)
//
// Key operations:
//
//   - Creation during authentication and registration
//   - Validation during token refresh
//   - Lookup during role resolution (SessionRoles service)
//   - Deletion during logout
//
// Expired sessions are automatically invalidated and removed.
//
// # Authorization and Roles
//
// The SessionRoles service extracts the session identifier from the JWT token,
// retrieves the session from storage, and maps it to a domain model containing
// user roles.
//
// This enables downstream services to perform authorization decisions based on roles.
//
// # Logout Flow
//
// The logout process:
//
//  1. Extracts the session identifier from the JWT token
//  2. Deletes the corresponding session from the repository
//
// This invalidates both access and refresh tokens associated with the session.
//
// # Transactions
//
// Critical operations (e.g., authentication and registration) are executed
// within database transactions to ensure consistency.
//
// The helper function Defer ensures that:
//
//   - Transactions are committed on success
//   - Transactions are rolled back on error
//   - Errors during commit/rollback are logged
//
// # Error Handling
//
// Errors are classified and normalized using the xerror package,
// allowing consistent handling of:
//
//   - Database errors (e.g., unique violations)
//   - Authentication failures
//   - Token parsing/validation issues
//   - Session-related errors
//
// Logging is performed using structured logging (log/slog).
//
// # Extensibility
//
// The package is designed with interfaces for repositories, enabling:
//
//   - Easy mocking for unit tests (via go:generate mockgen)
//   - Swappable storage implementations
//   - Clear separation of concerns
//
// # Testing
//
// Unit tests typically:
//
//   - Use generated mocks for repositories and database transactions
//   - Validate business logic independently of infrastructure
//   - Cover edge cases such as invalid tokens, missing sessions,
//     invalid credentials, and transaction failures
//
// # Security Considerations
//
//   - Passwords are never stored in plain text
//   - Tokens are signed using a configurable secret key (HS256)
//   - Refresh tokens should be stored securely (e.g., HTTP-only cookies)
//   - SameSite=Strict is recommended to mitigate CSRF attacks
//
// Note: In non-HTTPS environments, Secure cookies may not be transmitted.
// This should be configurable depending on deployment environment.
package services
