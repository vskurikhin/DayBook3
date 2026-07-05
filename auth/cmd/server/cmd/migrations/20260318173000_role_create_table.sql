-- +goose Up
SELECT 'up SQL query role create table';
CREATE TABLE IF NOT EXISTS role (
    role            VARCHAR(64) PRIMARY KEY     NOT NULL,
    id              UUID                        NOT NULL DEFAULT pg_catalog.uuidv7()
    CONSTRAINT UC_8532_auth_id_must_be_unique UNIQUE,
    description     VARCHAR(4096),
    user_name       VARCHAR(64)                 NOT NULL,
    CONSTRAINT FK_8952_auth_role_auth_user_name
        FOREIGN KEY (user_name)
            REFERENCES  user_name (user_name)
            ON DELETE CASCADE ON UPDATE CASCADE,
    create_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    update_time     TIMESTAMP WITHOUT TIME ZONE          DEFAULT now(),
    enabled         BOOLEAN                     NOT NULL DEFAULT true,
    local_change    BOOLEAN                     NOT NULL DEFAULT true,
    visible         BOOLEAN                     NOT NULL DEFAULT true,
    flags           INT                         NOT NULL DEFAULT 0
    );

-- +goose Down
SELECT 'down SQL query role table drop';
DROP TABLE IF EXISTS role;
