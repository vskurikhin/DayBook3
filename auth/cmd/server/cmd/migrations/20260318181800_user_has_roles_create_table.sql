-- +goose Up
SELECT 'up SQL query user_has_roles create table';
CREATE SEQUENCE IF NOT EXISTS user_has_roles_seq START 1;

CREATE TABLE IF NOT EXISTS user_has_roles
(
    id              BIGINT PRIMARY KEY          NOT NULL DEFAULT nextval('auth.user_has_roles_seq'),
    user_name       VARCHAR(64)                 NOT NULL,
      CONSTRAINT    FK_2751_auth_user_has_roles_auth_user_name
        FOREIGN KEY (user_name)
        REFERENCES  user_name (user_name)
        ON DELETE CASCADE ON UPDATE CASCADE,
    role            VARCHAR(64)                 NOT NULL,
      CONSTRAINT    FK_2751_auth_user_has_roles_auth_role
        FOREIGN KEY (role)
        REFERENCES  role (role)
        ON DELETE CASCADE ON UPDATE CASCADE,
    create_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    update_time     TIMESTAMP WITHOUT TIME ZONE          DEFAULT now(),
    enabled         BOOLEAN                     NOT NULL DEFAULT false,
    local_change    BOOLEAN                     NOT NULL DEFAULT true,
    visible         BOOLEAN                              DEFAULT true,
    flags           INT                         NOT NULL DEFAULT 0
    );


-- +goose Down
SELECT 'down SQL query user_has_roles table drop';
DROP TABLE IF EXISTS user_has_roles;
DROP SEQUENCE IF EXISTS user_has_roles_seq;
