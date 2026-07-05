--liquibase formatted sql
--

--
--changeset svn:10100 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10100_date_2026_03_26_time_08_40_00_issue_70_base_records_create.sql
--

--
CREATE TABLE IF NOT EXISTS core.base_records (
    id            UUID  PRIMARY KEY             NOT NULL    DEFAULT pg_catalog.uuidv7(),
    parent_id     UUID                          NOT NULL,
      FOREIGN KEY (parent_id)
      REFERENCES  core.base_records (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    type          SMALLINT                      NOT NULL    DEFAULT 0      CHECK (type BETWEEN 0 AND 7),
    user_name     VARCHAR(64)                   NOT NULL,
      CONSTRAINT  FK_605f_core_base_records_security_user_name
      FOREIGN KEY (user_name)
      REFERENCES  core.user_name (user_name)
        ON DELETE CASCADE ON UPDATE CASCADE,
    post_at       TIMESTAMP WITH TIME ZONE      NOT NULL    DEFAULT now(),
    refresh_at    TIMESTAMP WITH TIME ZONE                  DEFAULT now(),
    create_time   TIMESTAMP WITHOUT TIME ZONE   NOT NULL    DEFAULT now(),
    update_time   TIMESTAMP WITHOUT TIME ZONE               DEFAULT now(),
    enabled       BOOLEAN                                   DEFAULT true,
    local_change  BOOLEAN                       NOT NULL    DEFAULT true,
    visible       BOOLEAN                                   DEFAULT true,
    flags         INT                           NOT NULL    DEFAULT 0
    );

--
--rollback DROP TABLE IF EXISTS core.base_records;
