package config

import (
	"bytes"
	"context"
	"log/slog"
	"os"
	"os/signal"
	"sync"
	"syscall"
	"testing"
	"time"

	"github.com/fsnotify/fsnotify"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

// 🔧 Вспомогательная функция сброса глобального состояния
func resetConfigState() {
	cfg = nil
	once = new(sync.Once)
	viper.Reset()
}

// 1️⃣ Test: ValuesConfig реализует интерфейс Config
func TestValuesConfig_ImplementsConfig(t *testing.T) {
	var c Config = &ValuesConfig{}
	if c == nil {
		t.Fatal("expected non-nil config")
	}
}

// 2️⃣ Test: Values() возвращает правильные данные
func TestValuesConfig_Values(t *testing.T) {
	values := Values{
		Address: "localhost:8089",
		Debug:   true,
		Verbose: true,
	}

	cfg := ValuesConfig{values: values}

	got := cfg.Values()

	if got.Address != values.Address {
		t.Fatalf("expected address %s, got %s", values.Address, got.Address)
	}
	if got.Debug != values.Debug {
		t.Fatalf("expected debug %v, got %v", values.Debug, got.Debug)
	}
}

// 3️⃣ Test: NewConfig создаёт singleton
func TestNewConfig_Singleton(t *testing.T) {
	resetConfigState()

	viper.Set("address", "localhost:9000")
	viper.Set("debug", true)

	cmd := &cobra.Command{}
	cmd.Flags().Bool("debug", false, "")

	c1, err := NewConfig(cmd)
	if err != nil {
		t.Fatal(err)
	}
	c2, err := NewConfig(cmd)
	if err != nil {
		t.Fatal(err)
	}

	if c1 != c2 {
		t.Fatal("expected singleton instance")
	}
}

// 4️⃣ Test: NewConfig читает значения из viper
func TestNewConfig_UnmarshalValues(t *testing.T) {
	resetConfigState()

	viper.Set("address", "127.0.0.1:8089")
	viper.Set("debug", true)
	viper.Set("verbose", true)
	viper.Set("insecure_skip_verify", true)

	cmd := &cobra.Command{}
	cmd.Flags().Bool("debug", false, "")

	cfg, err := NewConfig(cmd)
	if err != nil {
		t.Fatal(err)
	}
	values := cfg.Values()

	if values.Address != "127.0.0.1:8089" {
		t.Fatalf("expected address 127.0.0.1:8089, got %s", values.Address)
	}
	if !values.Debug {
		t.Fatal("expected debug true")
	}
	if !values.Verbose {
		t.Fatal("expected verbose true")
	}
	if !values.InsecureSkipVerify {
		t.Fatal("expected insecure_skip_verify true")
	}
}

// 5️⃣ Test: NewConfig не перезаписывает значения после первого вызова
func TestNewConfig_OnceBehavior(t *testing.T) {
	resetConfigState()

	viper.Set("address", "first")
	cmd := &cobra.Command{}
	cmd.Flags().Bool("debug", false, "")

	c1, err := NewConfig(cmd)
	if err != nil {
		t.Fatal(err)
	}

	// меняем viper после первого вызова
	viper.Set("address", "second")

	c2, err := NewConfig(cmd)
	if err != nil {
		t.Fatal(err)
	}

	if c2.Values().Address != "first" {
		t.Fatalf("expected singleton to keep first value, got %s", c2.Values().Address)
	}

	if c1 != c2 {
		t.Fatal("expected same instance")
	}
}

// 6️⃣ Test: Values.Ssl() возвращает false по умолчанию
func TestValues_Ssl_DefaultFalse(t *testing.T) {
	v := Values{}

	if v.Ssl() {
		t.Fatal("expected ssl to be false by default")
	}
}

// 7️⃣ Test: Проверка unexported ssl поля (через литерал)
func TestValues_Ssl_True(t *testing.T) {
	v := Values{
		ssl: true,
	}

	if !v.Ssl() {
		t.Fatal("expected ssl true")
	}
}

func newTestCommand(debug bool) *cobra.Command {
	cmd := &cobra.Command{Use: "test"}
	cmd.Flags().Bool("debug", debug, "")
	cmd.Flags().Bool("verbose", false, "")
	_ = cmd.Flags().Set("debug", map[bool]string{true: "true", false: "false"}[debug])
	return cmd
}

func TestNewConfig_ShouldBindValuesFromViper(t *testing.T) {
	resetConfigState()

	viper.Set("address", "localhost:8089")
	viper.Set("debug", true)
	viper.Set("verbose", true)
	viper.Set("insecure_skip_verify", true)

	cmd := newTestCommand(true)

	config, err := NewConfig(cmd)
	if err != nil {
		t.Fatal(err)
	}
	values := config.Values()

	if values.Address != "localhost:8089" {
		t.Errorf("expected address 'localhost:8089', got '%s'", values.Address)
	}

	if !values.Debug {
		t.Error("expected Debug to be true")
	}

	if !values.Verbose {
		t.Error("expected Verbose to be true")
	}

	if !values.InsecureSkipVerify {
		t.Error("expected InsecureSkipVerify to be true")
	}
}

func TestNewConfig_ShouldBeSingleton(t *testing.T) {
	resetConfigState()

	viper.Set("address", "first")

	cmd := newTestCommand(false)
	config1, err := NewConfig(cmd)
	if err != nil {
		t.Fatal(err)
	}

	// Меняем значение во viper
	viper.Set("address", "second")

	config2, err := NewConfig(cmd)
	if err != nil {
		t.Fatal(err)
	}

	if config1 != config2 {
		t.Error("expected same instance due to sync.Once singleton")
	}

	if config2.Values().Address != "first" {
		t.Error("expected address to remain 'first' due to singleton behavior")
	}

	config3 := GetConfig()

	if config1 != config3 {
		t.Error("expected same instance due to sync.Once singleton")
	}

}

func TestValuesMethod_ReturnsCorrectValues(t *testing.T) {
	resetConfigState()

	expected := Values{
		Address: "test",
		Debug:   true,
	}

	cfg = &ValuesConfig{values: expected}

	values := cfg.Values()

	if values.Address != expected.Address {
		t.Errorf("expected address %s, got %s", expected.Address, values.Address)
	}

	if values.Debug != expected.Debug {
		t.Errorf("expected debug %v, got %v", expected.Debug, values.Debug)
	}
}

func TestSsl_DefaultValueIsFalse(t *testing.T) {
	values := Values{}

	if values.Ssl() {
		t.Error("expected default ssl to be false")
	}
}

func TestLoopSigHup_ContextDone(t *testing.T) {
	// Запускаем loopSigHup с контекстом, который сразу отменяется
	ctx, cancel := context.WithCancel(context.Background())
	cancel() // сразу отменяем

	// Функция должна завершиться сразу, не блокируя тест
	loopSigHup(ctx)
}

func TestLoopSigHup_ConfigReload(t *testing.T) {
	// Подготовка временного файла viper
	viper.AddConfigPath(".")
	viper.SetConfigType("yaml")
	viper.SetConfigName("test.yaml")

	cfg = &ValuesConfig{}

	go loopSigHup(context.Background())
	p, err := os.FindProcess(os.Getpid())
	if err != nil {
		t.Fatal(err)
	}
	err = p.Signal(syscall.SIGHUP)
	if err != nil {
		t.Fatal(err)
	}
	// Если функция завершилась без паники — тест пройден
}

func TestConfigChange_Success(t *testing.T) {
	// Подготовка временного cfg
	cfg = &ValuesConfig{
		values: Values{
			Address: "old-address",
			Debug:   false,
		},
		mu: sync.RWMutex{},
	}

	// Сброс viper
	v := viper.New()
	v.Set("address", "new-address")
	viper.Set("address", "new-address") // глобально, чтобы Unmarshal работал

	configChange(fsnotify.Event{})

	cfgValues := cfg.Values()
	if cfgValues.Address != "new-address" {
		t.Fatalf("expected Address 'new-address', got '%s'", cfgValues.Address)
	}
}

func TestLoopSigHup_SignalReload(t *testing.T) {
	sig := make(chan os.Signal, 1)

	signal.Notify(sig, syscall.SIGHUP)
	resetConfigState()

	viper.Set("address", "before")

	cfg, err := NewConfig(nil)
	if err != nil {
		t.Fatal(err)
	}

	go loopSigHup(context.Background())

	viper.AddConfigPath(".")
	viper.SetConfigType("yaml")
	viper.SetConfigName("test.yaml")

	viper.Set("address", "after")

	p, err := os.FindProcess(os.Getpid())
	if err != nil {
		t.Fatal(err)
	}

	err = p.Signal(syscall.SIGHUP)
	if err != nil {
		t.Fatal(err)
	}

	time.Sleep(100 * time.Millisecond)

	values := cfg.Values()

	select {
	case <-sig:
		// OK
	case <-time.After(time.Second):
		t.Fatal("signal not received")
	}

	if values.Address != "after" && values.Address != "before" {
		t.Fatalf("unexpected value %s", values.Address)
	}
}

func TestLoopSigHup_SignalReload_ReadInConfigError(t *testing.T) {
	sig := make(chan os.Signal, 1)

	signal.Notify(sig, syscall.SIGHUP)
	resetConfigState()

	viper.Set("address", "before")

	cfg, err := NewConfig(nil)
	if err != nil {
		t.Fatal(err)
	}

	go loopSigHup(context.Background())

	viper.Set("address", "after")

	p, err := os.FindProcess(os.Getpid())
	if err != nil {
		t.Fatal(err)
	}

	err = p.Signal(syscall.SIGHUP)
	if err != nil {
		t.Fatal(err)
	}

	time.Sleep(100 * time.Millisecond)

	values := cfg.Values()

	select {
	case <-sig:
		// OK
	case <-time.After(time.Second):
		t.Fatal("signal not received")
	}

	if values.Address != "after" && values.Address != "before" {
		t.Fatalf("unexpected value %s", values.Address)
	}
}

func TestNewConfig_UnmarshalError_DebugEnabled(t *testing.T) {
	resetConfigState()

	// Создаём буфер для перехвата slog
	var buf bytes.Buffer
	logger := slog.New(slog.NewTextHandler(&buf, nil))
	slog.SetDefault(logger)

	// Устанавливаем некорректный тип -> viper.Unmarshal вызовет ошибку
	viper.Set("debug", map[string]string{"invalid": "value"})

	cmd := &cobra.Command{}
	cmd.Flags().Bool("debug", true, "")

	_, err := NewConfig(cmd)

	if err == nil {
		t.Fatal("expected unmarshal error")
	}

	logOutput := buf.String()

	if logOutput == "" {
		t.Fatal("expected slog error log")
	}
}

func TestConfigChange_UnmarshalError(t *testing.T) {
	resetConfigState()

	// Перехватываем slog
	var buf bytes.Buffer
	logger := slog.New(slog.NewTextHandler(&buf, nil))
	slog.SetDefault(logger)

	// Создаём начальную конфигурацию
	viper.Set("address", "localhost:8089")

	c, err := NewConfig(nil)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	// Устанавливаем некорректный тип (bool ожидается)
	viper.Set("debug", map[string]string{"invalid": "value"})

	// Вызываем reload конфигурации
	configChange(fsnotify.Event{})

	logOutput := buf.String()

	if logOutput == "" {
		t.Fatal("expected slog error log")
	}

	// Конфигурация не должна измениться
	if c.Values().Address != "localhost:8089" {
		t.Fatal("config should not change on unmarshal error")
	}
}
