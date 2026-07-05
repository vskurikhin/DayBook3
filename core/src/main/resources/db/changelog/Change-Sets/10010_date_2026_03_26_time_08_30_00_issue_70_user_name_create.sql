--liquibase formatted sql
--

--
--changeset svn:10010 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10010_date_2026_03_26_time_08_30_00_issue_70_user_name_create.sql
--

--
CREATE SEQUENCE IF NOT EXISTS core.user_name_seq
    AS BIGINT
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS core.user_name(
    user_name       VARCHAR(64) PRIMARY KEY     NOT NULL,
    id              UUID                        NOT NULL,
    sequence_id     BIGINT                      NOT NULL
      CONSTRAINT    user_name_sequence_id_must_be_unique UNIQUE,
    create_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    update_time     TIMESTAMP WITHOUT TIME ZONE          DEFAULT now(),
    enabled         BOOLEAN                     NOT NULL DEFAULT true,
    local_change    BOOLEAN                     NOT NULL DEFAULT true,
    visible         BOOLEAN                     NOT NULL DEFAULT true,
    flags           INT                         NOT NULL DEFAULT 0
    );

CREATE UNIQUE INDEX IF NOT EXISTS IDX_UC_3109_core_id_must_be_unique
    ON core.user_name (id);

--
--rollback DROP INDEX IF EXISTS IDX_UC_3109_core_id_must_be_unique;
--rollback DROP TABLE IF EXISTS core.user_name;
--rollback DROP SEQUENCE IF EXISTS core.user_name_seq;
