-- +goose Up
SELECT 'up SQL query user_attrs create table';
CREATE TABLE IF NOT EXISTS user_attrs (
    user_name       VARCHAR(64) PRIMARY KEY     NOT NULL,
    CONSTRAINT FK_1373_auth_user_attrs_auth_user_name
        FOREIGN KEY (user_name)
            REFERENCES user_name (user_name)
            ON DELETE CASCADE ON UPDATE CASCADE,
    attrs           JSONB                       NOT NULL,
    name            VARCHAR(1024)               NOT NULL,
    create_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    update_time     TIMESTAMP WITHOUT TIME ZONE          DEFAULT now(),
    enabled         BOOLEAN                     NOT NULL DEFAULT true,
    local_change    BOOLEAN                     NOT NULL DEFAULT true,
    visible         BOOLEAN                     NOT NULL DEFAULT true,
    flags           INT                         NOT NULL DEFAULT 0
    );

-- +goose Down
SELECT 'down SQL query user_attrs table drop';
DROP TABLE IF EXISTS user_attrs;