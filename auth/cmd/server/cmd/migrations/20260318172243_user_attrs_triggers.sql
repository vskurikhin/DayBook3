-- +goose Up
SELECT 'up SQL query user_attrs create triggers';
DROP TRIGGER IF EXISTS update_create_time_auth_user_attrs ON auth.user_attrs;
CREATE TRIGGER update_create_time_auth_user_attrs
    BEFORE INSERT
    ON auth.user_attrs
    FOR EACH ROW
    EXECUTE FUNCTION auth.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_auth_user_attrs ON auth.user_attrs;
CREATE TRIGGER update_update_time_auth_user_attrs
    BEFORE UPDATE
    ON auth.user_attrs
    FOR EACH ROW
    EXECUTE FUNCTION auth.update_update_time();

-- +goose Down
SELECT 'down SQL query user_attrs triggers drop';
DROP TRIGGER IF EXISTS update_update_time_auth_user_attrs ON auth.user_attrs;
DROP TRIGGER IF EXISTS update_create_time_auth_user_attrs ON auth.user_attrs;