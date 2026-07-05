-- +goose Up
SELECT 'up SQL query session create triggers';
CREATE TABLE IF NOT EXISTS session (
    iss          UUID                        NOT NULL,
    jti          UUID                        NOT NULL,
    sub          UUID                        NOT NULL,
    user_name    VARCHAR(64)                 NOT NULL,
      CONSTRAINT FK_8053_auth_session_auth_user_name
          FOREIGN KEY (user_name)
              REFERENCES user_name (user_name)
              ON DELETE CASCADE ON UPDATE CASCADE,
    roles        TEXT[]                      NOT NULL,
    valid_time   TIMESTAMP WITH    TIME ZONE NOT NULL DEFAULT now(),
    create_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    update_time  TIMESTAMP WITHOUT TIME ZONE          DEFAULT now(),
    enabled      BOOLEAN                     NOT NULL DEFAULT false,
    local_change BOOLEAN                     NOT NULL DEFAULT true,
    visible      BOOLEAN                              DEFAULT true,
    flags        INT                         NOT NULL DEFAULT 0,
    PRIMARY KEY (iss, jti, sub)
);

-- +goose Down
SELECT 'down SQL query session table drop';
DROP TABLE IF EXISTS session;
