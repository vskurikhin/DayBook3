package cmd

import (
	"os"
	"path/filepath"
	"testing"

	"github.com/spf13/viper"
)

// Helper
func resetState() {
	viper.Reset()
	cfgFile = ""
}

// Проверка persistent flags
func TestRootCmd_PersistentFlagsRegistered(t *testing.T) {
	flags := rootCmd.PersistentFlags()

	expected := []string{
		"config",
		FlagDebug,
		FlagVerbose,
	}

	for _, name := range expected {
		if flags.Lookup(name) == nil {
			t.Errorf("expected persistent flag %s to be registered", name)
		}
	}
}

func TestRunCmd_FlagsRegistered(t *testing.T) {
	flags := runCmd.Flags()

	expected := []string{
		FlagAddress,
		FlagInsecureSkipVerify,
	}

	for _, name := range expected {
		if flags.Lookup(name) == nil {
			t.Errorf("expected runCmd flag %s to be registered", name)
		}
	}
}

func TestRunCmd_AddedToRoot(t *testing.T) {
	found := false

	for _, cmd := range rootCmd.Commands() {
		if cmd == runCmd {
			found = true
			break
		}
	}

	if !found {
		t.Error("runCmd was not added to rootCmd")
	}
}

func TestInitConfig_WithExplicitFile(t *testing.T) {
	resetState()

	tmpDir := t.TempDir()
	configPath := filepath.Join(tmpDir, "test.yaml")

	err := os.WriteFile(configPath, []byte("address: localhost:8089\n"), 0644)
	if err != nil {
		t.Fatal(err)
	}

	cfgFile = configPath

	initConfig()

	if viper.ConfigFileUsed() != configPath {
		t.Errorf("expected config file %s, got %s",
			configPath, viper.ConfigFileUsed())
	}

	if viper.GetString("address") != "localhost:8089" {
		t.Errorf("expected address from config file")
	}
}

func TestInitConfig_UsesHomeDirectory(t *testing.T) {
	resetState()

	tmpDir := t.TempDir()
	t.Setenv("HOME", tmpDir)

	configPath := filepath.Join(tmpDir, ".auth-server.yaml")
	err := os.WriteFile(configPath, []byte("debug: true\n"), 0644)
	if err != nil {
		t.Fatal(err)
	}

	initConfig()

	if viper.GetBool("debug") != true {
		t.Errorf("expected debug=true from home config")
	}
}

func TestInitConfig_ReadsEnv(t *testing.T) {
	resetState()

	t.Setenv("ADDRESS", "env-host:9999")

	initConfig()

	if viper.GetString("ADDRESS") != "env-host:9999" {
		t.Errorf("expected ADDRESS to be read from env")
	}
}
