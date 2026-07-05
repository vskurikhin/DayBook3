package main

import (
	"fmt"
	"net"
	"os"
	"os/exec"
	"syscall"
	"testing"
	"time"

	"github.com/mitchellh/go-ps"
)

func TestMain(m *testing.M) {
	fmt.Println("Before all tests (setup)")

	// Call m.Run() to run the tests in the package
	exitCode := m.Run()

	fmt.Println("After all tests (teardown)")

	// Exit with the code returned by m.Run()
	os.Exit(exitCode)
}

func TestWithAddressArgs(t *testing.T) {
	defer func() {
		if err := recover(); err != nil {
			if fmt.Sprintf("%s", err) != "unexpected call to os.Exit(0) during test" {
				t.Fatal("expected call to os.Exit(0) during test but error:", err)
			}
		}
	}()
	os.Args = []string{"main"}
	main()
}

func TestMainLogicExitCode(t *testing.T) {
	t.Skip()
	var cmd *exec.Cmd
	done := make(chan struct{})
	go func() {
		cmd = exec.Command("go", "run", "main.go", "run", "--address", freeAddr(t))
		output, _ := cmd.CombinedOutput()
		_, _ = fmt.Fprintf(os.Stderr, "output: %s\n", string(output))
		close(done)
	}()
	time.Sleep(900 * time.Millisecond)
	parentPID := cmd.Process.Pid
	time.Sleep(500 * time.Millisecond)

	// Получаем все процессы
	processList, err := ps.Processes()
	if err != nil {
		fmt.Println("Error:", err)
	}

	fmt.Printf("Child processes of PID %d:\n", parentPID)
	for _, proc := range processList {
		if proc.PPid() == parentPID && proc.Executable() == "main" {
			_, _ = fmt.Fprintf(os.Stderr, "- PID: %d, Name: %s\n", proc.Pid(), proc.Executable())
			_ = syscall.Kill(proc.Pid(), syscall.SIGINT)
		}
	}

	select {
	case <-done:
		// success
	case <-time.After(2 * time.Second):
		// skip
	}
}

func freeAddr(t *testing.T) string {
	l, err := net.Listen("tcp", "127.0.0.1:0")
	if err != nil {
		t.Fatal(err)
	}
	defer func() { _ = l.Close() }()
	return l.Addr().String()
}
