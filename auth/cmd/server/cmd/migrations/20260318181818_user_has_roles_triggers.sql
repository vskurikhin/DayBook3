-- +goose Up
SELECT 'up SQL query user_has_roles create triggers';
DROP TRIGGER IF EXISTS update_create_time_auth_user_has_roles ON auth.user_has_roles;
CREATE TRIGGER update_create_time_auth_user_has_roles
    BEFORE INSERT
    ON auth.user_has_roles
    FOR EACH ROW
    EXECUTE FUNCTION auth.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_auth_user_has_roles ON auth.user_has_roles;
CREATE TRIGGER update_update_time_auth_user_has_roles
    BEFORE UPDATE
    ON auth.user_has_roles
    FOR EACH ROW
    EXECUTE FUNCTION auth.update_update_time();

-- +goose Down
SELECT 'down SQL query user_has_roles triggers drop';
DROP TRIGGER IF EXISTS update_update_time_auth_user_has_roles ON auth.user_has_roles;
DROP TRIGGER IF EXISTS update_create_time_auth_user_has_roles ON auth.user_has_roles;
