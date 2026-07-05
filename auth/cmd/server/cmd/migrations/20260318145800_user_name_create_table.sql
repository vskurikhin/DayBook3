-- +goose Up
SELECT 'up SQL query user_name create table';
CREATE TABLE IF NOT EXISTS user_name
(
    user_name       VARCHAR(64) PRIMARY KEY     NOT NULL,
    id              UUID                        NOT NULL DEFAULT pg_catalog.uuidv7()
      CONSTRAINT UC_3109_auth_id_must_be_unique UNIQUE,
    password        VARCHAR(1024)               NOT NULL,
    create_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    update_time     TIMESTAMP WITHOUT TIME ZONE          DEFAULT now(),
    enabled         BOOLEAN                     NOT NULL DEFAULT true,
    local_change    BOOLEAN                     NOT NULL DEFAULT true,
    visible         BOOLEAN                     NOT NULL DEFAULT true,
    flags           INT                         NOT NULL DEFAULT 0
    );

CREATE INDEX IF NOT EXISTS IDX_auth_user_name_id
    ON user_name (id);

-- +goose Down
SELECT 'down SQL query user_name table drop';
DROP INDEX IF EXISTS IDX_auth_user_name_id;
DROP TABLE IF EXISTS user_name;
