--liquibase formatted sql
--

--
--changeset svn:10176 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10176_date_2026_05_22_time_09_48_26_issue_119_text_records_create.sql
--

--
CREATE TABLE IF NOT EXISTS core.text_records (
    id            UUID  PRIMARY KEY             NOT NULL    DEFAULT pg_catalog.uuidv7(),
      CONSTRAINT  FK_9fa9_core_text_records_base_records_id
      FOREIGN KEY (id)
      REFERENCES  core.base_records (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    title         VARCHAR(4096),
    value         VARCHAR(10485760)             NOT NULL,
    type          SMALLINT                      NOT NULL    DEFAULT 0      CHECK (type BETWEEN 0 AND 3),
    user_name     VARCHAR(64)                   NOT NULL,
      CONSTRAINT  FK_1877_core_text_records_security_user_name
      FOREIGN KEY (user_name)
      REFERENCES  core.user_name (user_name)
        ON DELETE CASCADE ON UPDATE CASCADE,
    create_time   TIMESTAMP WITHOUT TIME ZONE   NOT NULL    DEFAULT now(),
    update_time   TIMESTAMP WITHOUT TIME ZONE               DEFAULT now(),
    enabled       BOOLEAN                                   DEFAULT true,
    local_change  BOOLEAN                       NOT NULL    DEFAULT true,
    visible       BOOLEAN                                   DEFAULT true,
    flags         INT                           NOT NULL    DEFAULT 0
    );

--
--rollback DROP TABLE IF EXISTS core.text_records;
