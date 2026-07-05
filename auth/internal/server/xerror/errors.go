package xerror

import (
	"errors"
	"fmt"
	"log/slog"
	"net/http"

	"github.com/jackc/pgx/v5"
)

const (
	ConnectionException                     = "connection exception"
	DeadlockDetected                        = "deadlock detected"
	Forbidden                               = "forbidden"
	InsufficientResources                   = "insufficient resources"
	InvalidName                             = "invalid name"
	InvalidPasswordError                    = "invalid password"
	InvalidToken                            = "invalid token"
	InvalidTokenClaims                      = "invalid token claims"
	InvalidTransactionInitiation            = "invalid transaction"
	InvalidUserID                           = "invalid user id"
	InvalidUserName                         = "invalid user name"
	IsNotPgError                            = "isn't pg error"
	LimitExceeded                           = "limit exceeded"
	Logout                                  = "logout"
	NoRowsInResultSet                       = "no rows in result set"
	PasswordNotValidError                   = "password not valid"
	SerializationFailure                    = "serialization failure"
	SessionTimeExpired                      = "session time expired"
	SyntaxError                             = "syntax error"
	SystemError                             = "system error"
	TransactionIntegrityConstraintViolation = "transaction integrity constraint violation"
	TransactionRollback                     = "transaction rollback"
	TriggeredActionException                = "tr action exception"
	UnclassifiedPgError                     = "unclassified"
	UndefinedColumn                         = "undefined column"
	UndefinedFunction                       = "undefined function"
	UndefinedParameter                      = "undefined parameter"
	UndefinedTable                          = "undefined table"
	UniqueViolation                         = "unique violation"
	UserExists                              = "user exists"
)

type PgError interface {
	Error() string
	SQLState() string
}

var (
	ErrConnectionException                           = errors.New(ConnectionException)
	ErrDeadlockDetected                              = errors.New(DeadlockDetected)
	ErrForbidden                                     = errors.New(Forbidden)
	ErrInsufficientResources                         = errors.New(InsufficientResources)
	ErrInvalidName                                   = errors.New(InvalidName)
	ErrInvalidPassword                               = errors.New(InvalidPasswordError)
	ErrInvalidToken                                  = errors.New(InvalidToken)
	ErrInvalidTokenClaims                            = errors.New(InvalidTokenClaims)
	ErrInvalidTransactionInitiation                  = errors.New(InvalidTransactionInitiation)
	ErrIsNotPgError                                  = errors.New(IsNotPgError)
	ErrJInvalidUserName                              = errors.New(InvalidUserName)
	ErrLimitExceeded                                 = errors.New(LimitExceeded)
	ErrLogout                                        = errors.New(Logout)
	ErrNil                                     error = nil
	ErrNoCookie                                      = errors.New("no cookie")
	ErrNoRows                                        = errors.New(NoRowsInResultSet)
	ErrPasswordNotValid                              = errors.New(PasswordNotValidError)
	ErrSerializationFailure                          = errors.New(SerializationFailure)
	ErrSessionTimeExpired                            = errors.New(SessionTimeExpired)
	ErrSyntaxError                                   = errors.New(SyntaxError)
	ErrSystemError                                   = errors.New(SystemError)
	ErrTransactionIntegrityConstraintViolation       = errors.New(TransactionIntegrityConstraintViolation)
	ErrTransactionRollback                           = errors.New(TransactionRollback)
	ErrTriggeredActionException                      = errors.New(TriggeredActionException)
	ErrUnclassifiedPgError                           = errors.New(UnclassifiedPgError)
	ErrUndefinedColumn                               = errors.New(UndefinedColumn)
	ErrUndefinedFunction                             = errors.New(UndefinedFunction)
	ErrUndefinedParameter                            = errors.New(UndefinedParameter)
	ErrUndefinedTable                                = errors.New(UndefinedTable)
	ErrUniqueViolation                               = errors.New(UniqueViolation)
	ErrUserExists                                    = errors.New(UserExists)
)

func IsPgError(err error) bool {
	var pgError PgError
	return errors.As(err, &pgError)
}

func ClassingHTTPError(err error) error {
	if errors.Is(err, http.ErrNoCookie) {
		return ErrNoCookie
	}
	return ErrUnclassifiedPgError
}

func ClassingPgError(err error) error {
	if errors.Is(err, pgx.ErrNoRows) {
		return ErrNoRows
	}
	var pgError PgError
	if !errors.As(err, &pgError) {
		slog.Error(
			"error classing pg",
			slog.String("errorType", fmt.Sprintf("%T", err)),
			slog.String("error", err.Error()),
		)
		return ErrIsNotPgError
	}
	switch pgError.SQLState() {
	case "08000", "08001", "08003", "08004", "08006", "08007", "08008", "08009", "08P01":
		return ErrConnectionException
	case "09000":
		return ErrTriggeredActionException
	case "0B000", "0P000":
		return ErrInvalidTransactionInitiation
	case "23505": // unique_violation
		return ErrUniqueViolation
	case "40000", "40003":
		return ErrTransactionRollback
	case "40001":
		return ErrSerializationFailure
	case "40002":
		return ErrTransactionIntegrityConstraintViolation
	case "40P01":
		return ErrDeadlockDetected
	case "42000", "42601", "42846", "42803", "42P20":
		return ErrSyntaxError
	case "42501":
		return ErrForbidden
	case "42602":
		return ErrInvalidName
	case "42703": // undefined_column
		return ErrUndefinedColumn
	case "42883": // undefined_function
		return ErrUndefinedFunction
	case "42P01": // undefined_table
		return ErrUndefinedTable
	case "42P02": // undefined_parameter
		return ErrUndefinedParameter
	case "53000", "53100", "53200", "53300", "53400":
		return ErrInsufficientResources
	case "54000", "54001", "54011", "54023":
		return ErrLimitExceeded
	case "58000", "58001", "58002", "58011", "58023", "58030", "58P01", "58P02", "58P03":
		return ErrSystemError
	default:
		return ErrUnclassifiedPgError
	}
}
