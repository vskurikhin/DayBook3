package tool

import (
	"testing"

	"github.com/spf13/cobra"
)

func TestIsDebug(t *testing.T) {
	cmd := &cobra.Command{}
	cmd.Flags().Bool("debug", false, "debug flag")

	// по умолчанию false
	if IsDebug(cmd) {
		t.Error("expected IsDebug to return false by default")
	}

	// установим true
	cmd.Flags().Set("debug", "true")
	if !IsDebug(cmd) {
		t.Error("expected IsDebug to return true after setting flag")
	}

	// установим false
	cmd.Flags().Set("debug", "false")
	if IsDebug(cmd) {
		t.Error("expected IsDebug to return false after resetting flag")
	}
}

func TestIsVerbose(t *testing.T) {
	cmd := &cobra.Command{}
	cmd.Flags().Bool("verbose", false, "verbose flag")

	// по умолчанию false
	if IsVerbose(cmd) {
		t.Error("expected IsVerbose to return false by default")
	}

	// установим true
	cmd.Flags().Set("verbose", "true")
	if !IsVerbose(cmd) {
		t.Error("expected IsVerbose to return true after setting flag")
	}

	// установим false
	cmd.Flags().Set("verbose", "false")
	if IsVerbose(cmd) {
		t.Error("expected IsVerbose to return false after resetting flag")
	}
}
