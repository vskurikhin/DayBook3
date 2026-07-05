-- +goose Up
SELECT 'up SQL query role create triggers';
DROP TRIGGER IF EXISTS update_create_time_auth_role ON auth.role;
CREATE TRIGGER update_create_time_auth_role
    BEFORE INSERT
    ON auth.role
    FOR EACH ROW
    EXECUTE FUNCTION auth.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_auth_role ON auth.role;
CREATE TRIGGER update_update_time_auth_role
    BEFORE UPDATE
    ON auth.role
    FOR EACH ROW
    EXECUTE FUNCTION auth.update_update_time();

-- +goose Down
SELECT 'down SQL query role triggers drop';
DROP TRIGGER IF EXISTS update_update_time_auth_role ON auth.role;
DROP TRIGGER IF EXISTS update_create_time_auth_role ON auth.role;
