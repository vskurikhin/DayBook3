package cmd

import (
	"context"
	"errors"
	"sync"
	"testing"
	"time"

	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

// Проверка метаданных rootCmd
func TestRootCmd_Metadata(t *testing.T) {
	if rootCmd.Use != "auth-server" {
		t.Fatalf("expected Use 'auth-server', got %s", rootCmd.Use)
	}

	if rootCmd.Short == "" {
		t.Fatal("Short must not be empty")
	}

	if rootCmd.Long == "" {
		t.Fatal("Long must not be empty")
	}
}

// Smoke-тест Run (без panic)
func TestRootCmd_Run_ShouldNotPanic(t *testing.T) {
	cmd := &cobra.Command{}
	cmd.Flags().Bool("verbose", false, "")
	cmd.Flags().Bool("debug", false, "")

	err := rootCmd.RunE(cmd, []string{})
	if err != nil {
		t.Fatal(err)
	}
}

// slogInfoVerbose — verbose=false
func TestSlogInfoVerbose_VerboseFalse(t *testing.T) {
	viper.Reset()

	cmd := &cobra.Command{}
	cmd.Flags().Bool("verbose", false, "")
	_ = cmd.Flags().Set("verbose", "false")

	slogInfoVerbose(cmd) // просто проверяем отсутствие panic
}

// slogInfoVerbose — verbose=true
func TestSlogInfoVerbose_VerboseTrue(t *testing.T) {
	viper.Reset()
	viper.Set("key1", "value1")
	viper.Set("key2", 123)

	cmd := &cobra.Command{}
	cmd.Flags().Bool("verbose", true, "")
	_ = cmd.Flags().Set("verbose", "true")

	slogInfoVerbose(cmd) // отсутствие panic
}

// Execute — успешное завершение (код ExitCodeOK)
func TestExecute_ShouldReturnZeroOnSuccess(t *testing.T) {
	orig := executeCmd
	defer func() { executeCmd = orig }()

	executeCmd = func(ctx context.Context) error {
		return nil
	}

	ctx := context.Background()
	code := Execute(ctx)

	if code != ExitCodeOK {
		t.Fatalf("expected %d, got %d", ExitCodeOK, code)
	}
}

// Execute — ошибка команды (код ExitCodeError)
func TestExecute_ShouldReturnOneOnError(t *testing.T) {
	orig := executeCmd
	defer func() { executeCmd = orig }()

	executeCmd = func(ctx context.Context) error {
		return errors.New("boom")
	}

	ctx := context.Background()
	code := Execute(ctx)

	if code != ExitCodeError {
		t.Fatalf("expected %d, got %d", ExitCodeError, code)
	}
}

// Execute — отмена через context (код ExitCodeContextDone)
func TestExecute_ShouldReturnTwoOnContextCancel(t *testing.T) {
	orig := executeCmd
	defer func() { executeCmd = orig }()

	executeCmd = func(ctx context.Context) error {
		time.Sleep(200 * time.Millisecond)
		return nil
	}

	ctx, cancel := context.WithCancel(context.Background())

	var wg sync.WaitGroup
	wg.Add(1)

	var result int
	go func() {
		defer wg.Done()
		result = Execute(ctx)
	}()

	time.Sleep(50 * time.Millisecond)
	cancel()

	wg.Wait()

	if result != ExitCodeContextDone {
		t.Fatalf("expected %d, got %d", ExitCodeContextDone, result)
	}
}

// Execute — завершение по ctx.Done
func TestExecute_ShouldReturnOneOnContextCancel(t *testing.T) {
	origExec := executeCmd
	defer func() { executeCmd = origExec }()

	// Имитируем долгую команду
	executeCmd = func(ctx context.Context) error {
		time.Sleep(200 * time.Millisecond)
		return nil
	}

	ctx, cancel := context.WithCancel(context.Background())

	var wg sync.WaitGroup
	wg.Add(1)

	var result int
	go func() {
		defer wg.Done()
		result = Execute(ctx)
	}()

	time.Sleep(50 * time.Millisecond)
	cancel()

	wg.Wait()

	if result != ExitCodeContextDone {
		t.Fatalf("expected exit code %d, got %d", ExitCodeContextDone, result)
	}
}

// Execute — команда возвращает ошибку
func TestExecute_ShouldReturnOneWhenCommandReturnsError(t *testing.T) {
	origExec := executeCmd
	defer func() { executeCmd = origExec }()

	executeCmd = func(ctx context.Context) error {
		return errors.New("some error")
	}

	ctx := context.Background()
	code := Execute(ctx)

	if code != ExitCodeError {
		t.Fatalf("expected exit code %d, got %d", ExitCodeError, code)
	}
}
