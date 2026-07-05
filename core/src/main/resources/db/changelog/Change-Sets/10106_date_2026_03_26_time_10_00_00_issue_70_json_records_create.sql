--liquibase formatted sql
--

--
--changeset svn:10106 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10106_date_2026_03_26_time_10_00_00_issue_70_json_records_create.sql
--

--
CREATE TABLE IF NOT EXISTS core.json_records (
    id            UUID  PRIMARY KEY             NOT NULL    DEFAULT pg_catalog.uuidv7(),
      CONSTRAINT  FK_f134_core_json_records_base_records_id
      FOREIGN KEY (id)
      REFERENCES  core.base_records (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    title         VARCHAR(4096),
    values        JSONB,
    user_name     VARCHAR(64)                   NOT NULL,
      CONSTRAINT  FK_a412_core_json_records_security_user_name
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
--rollback DROP TABLE IF EXISTS core.json_records;
