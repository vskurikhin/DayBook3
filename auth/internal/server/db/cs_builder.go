package db

import (
	"errors"
	"fmt"
	"time"
)

type ConnectionStringBuilder struct {
	dbHost     string
	dbName     string
	dbPassword string
	dbPort     string
	dbUser     string
	separator  string
	tail       string
}

func (b ConnectionStringBuilder) WithDBOptions(options string) ConnectionStringBuilder {
	if options != "" {
		b.tail += b.separator + options
		b.separator = "&"
	}
	return b
}

func (b ConnectionStringBuilder) WithDBPoolMaxConns(poolMaxConns int) ConnectionStringBuilder {
	if poolMaxConns > 0 {
		b.tail += fmt.Sprintf("%spool_max_conns=%d", b.separator, poolMaxConns)
		b.separator = "&"
	}
	return b
}

func (b ConnectionStringBuilder) WithDBPoolMinConns(poolMinConns int) ConnectionStringBuilder {
	if poolMinConns > -1 {
		b.tail += fmt.Sprintf("%spool_min_conns=%d", b.separator, poolMinConns)
		b.separator = "&"
	}
	return b
}

func (b ConnectionStringBuilder) WithDBPoolMaxConnLifeTime(poolMaxConnLifeTime time.Duration) ConnectionStringBuilder {
	if poolMaxConnLifeTime > 100*time.Millisecond {
		b.tail += fmt.Sprintf("%spool_max_conn_lifetime=%v", b.separator, poolMaxConnLifeTime)
		b.separator = "&"
	}
	return b
}

func (b ConnectionStringBuilder) WithDBPoolMaxConnIdleTime(poolMaxConnIdleTime time.Duration) ConnectionStringBuilder {
	if poolMaxConnIdleTime > 100*time.Millisecond {
		b.tail += fmt.Sprintf("%spool_max_conn_idle_time=%v", b.separator, poolMaxConnIdleTime)
		b.separator = "&"
	}
	return b
}

func (b ConnectionStringBuilder) WithDBPoolHealthCheckPeriod(poolHealthCheckPeriod time.Duration) ConnectionStringBuilder {
	if poolHealthCheckPeriod > 100*time.Millisecond {
		b.tail += fmt.Sprintf("%spool_health_check_period=%v", b.separator, poolHealthCheckPeriod)
		b.separator = "&"
	}
	return b
}

var (
	ErrNotReady = errors.New("pgx DB pool connection string not ready")
)

func (b ConnectionStringBuilder) Build() (string, error) {
	if b.dbUser == "" {
		return "", fmt.Errorf("dbuser empty, error: %w", ErrNotReady)
	}
	if b.dbPassword == "" {
		return "", fmt.Errorf("dbpassword empty, error: %w", ErrNotReady)
	}
	if b.dbHost == "" {
		return "", fmt.Errorf("dbhost empty, error: %w", ErrNotReady)
	}
	if b.dbPort == "" {
		return "", fmt.Errorf("dbport empty, error: %w", ErrNotReady)
	}
	if b.dbName == "" {
		return "", fmt.Errorf("dbname empty, error: %w", ErrNotReady)
	}
	connString := fmt.Sprintf("postgres://%s:%s@%s:%s/%s", b.dbUser, b.dbPassword, b.dbHost, b.dbPort, b.dbName)
	if b.tail != "" {
		connString += b.tail
	}
	return connString, nil
}

func ConstructConnectionStringBuilder(opts ...func(*ConnectionStringBuilder)) ConnectionStringBuilder {
	var f = new(ConnectionStringBuilder)
	f.separator = "?"
	for _, opt := range opts {
		opt(f)
	}
	return *f
}

func WithDBHost(host string) func(*ConnectionStringBuilder) {
	return func(f *ConnectionStringBuilder) {
		if host != "" {
			f.dbHost = host
		}
	}
}

func WithDBName(name string) func(*ConnectionStringBuilder) {
	return func(f *ConnectionStringBuilder) {
		if name != "" {
			f.dbName = name
		}
	}
}

func WithDBPassword(password string) func(*ConnectionStringBuilder) {
	return func(f *ConnectionStringBuilder) {
		if password != "" {
			f.dbPassword = password
		}
	}
}

func WithDBPort(port string) func(*ConnectionStringBuilder) {
	return func(f *ConnectionStringBuilder) {
		if port != "" {
			f.dbPort = port
		}
	}
}

func WithDBUser(user string) func(*ConnectionStringBuilder) {
	return func(f *ConnectionStringBuilder) {
		if user != "" {
			f.dbUser = user
		}
	}
}
