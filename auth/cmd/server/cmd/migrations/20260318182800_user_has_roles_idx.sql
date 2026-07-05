-- +goose Up
SELECT 'up SQL query user_has_roles create table indexes';
CREATE INDEX IF NOT EXISTS IDX_auth_user_has_roles_user_name
    ON auth.user_has_roles (user_name);

CREATE UNIQUE INDEX IF NOT EXISTS IDX_auth_user_has_roles_user_name_role
    ON auth.user_has_roles (user_name, role);

ALTER TABLE auth.user_has_roles
    ADD CONSTRAINT UNQ_2751_auth_user_has_roles_user_name_role
        UNIQUE USING INDEX IDX_auth_user_has_roles_user_name_role;

-- +goose Down
SELECT 'down SQL query user_has_roles indexes drop';
ALTER TABLE IF EXISTS auth.user_has_roles DROP CONSTRAINT UNQ_2751_auth_user_has_roles_user_name_role;
DROP INDEX IF EXISTS auth.IDX_auth_user_has_roles_user_name_role;
DROP INDEX IF EXISTS auth.IDX_auth_user_has_roles_user_name;
