--liquibase formatted sql
--

--
--changeset svn:10024 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10024_date_2026_04_02_time_10_44_07_issue_73_records_create.sql
--

--

CREATE SEQUENCE IF NOT EXISTS api.post_records_seq
    AS BIGINT
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS api.post_records (
    id                  UUID  PRIMARY KEY             NOT NULL,
    parent_id           UUID                          NOT NULL,
      FOREIGN KEY       (parent_id)
      REFERENCES        api.post_records (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    sequence_id         BIGINT                        NOT NULL
      CONSTRAINT        post_records_sequence_id_must_be_unique   UNIQUE,
    type                SMALLINT                      NOT NULL    DEFAULT 0      CHECK (type BETWEEN 0 AND 7),
    user_name           VARCHAR(64)                   NOT NULL,
      CONSTRAINT        FK_605f_core_base_records_security_user_name
      FOREIGN KEY       (user_name)
      REFERENCES        api.user_name (user_name)
        ON DELETE CASCADE ON UPDATE CASCADE,
    post_at             TIMESTAMP WITH TIME ZONE      NOT NULL    DEFAULT now(),
    refresh_at          TIMESTAMP WITH TIME ZONE                  DEFAULT now(),
    create_time         TIMESTAMP WITHOUT TIME ZONE   NOT NULL    DEFAULT now(),
    update_time         TIMESTAMP WITHOUT TIME ZONE               DEFAULT now(),
    last_changed_time   TIMESTAMP WITHOUT TIME ZONE   NOT NULL,
    enabled             BOOLEAN                                   DEFAULT true,
    local_change        BOOLEAN                       NOT NULL    DEFAULT true,
    visible             BOOLEAN                                   DEFAULT true,
    flags               INT                           NOT NULL    DEFAULT 0,
    title               VARCHAR(4096),
    values              JSONB
    );

--
--rollback DROP TABLE IF EXISTS api.post_records;
--rollback DROP SEQUENCE IF EXISTS api.post_records_seq;
