--liquibase formatted sql
--

--
--changeset svn:10016 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10016_date_2026_04_01_time_16_47_16_issue_73_user_name_create.sql
--

--

CREATE SEQUENCE IF NOT EXISTS api.user_name_seq
    AS BIGINT
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS api.user_name(
    user_name       VARCHAR(64) PRIMARY KEY                 NOT NULL,
    id              UUID                                    NOT NULL,
    sequence_id     BIGINT                                  NOT NULL
      CONSTRAINT    user_name_sequence_id_must_be_unique    UNIQUE,
    roles           TEXT[],
    create_time     TIMESTAMP WITHOUT TIME ZONE             NOT NULL DEFAULT now(),
    update_time     TIMESTAMP WITHOUT TIME ZONE                      DEFAULT now(),
    enabled         BOOLEAN                                 NOT NULL DEFAULT true,
    local_change    BOOLEAN                                 NOT NULL DEFAULT true,
    visible         BOOLEAN                                 NOT NULL DEFAULT true,
    flags           INT                                     NOT NULL DEFAULT 0
    );

CREATE UNIQUE INDEX IF NOT EXISTS IDX_UC_1340_api_id_must_be_unique
    ON api.user_name (id);

--
--rollback DROP INDEX IF EXISTS IDX_UC_1340_api_id_must_be_unique;
--rollback DROP TABLE IF EXISTS api.user_name;
