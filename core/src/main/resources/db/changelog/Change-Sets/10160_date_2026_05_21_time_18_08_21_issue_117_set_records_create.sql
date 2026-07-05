--liquibase formatted sql
--

--
--changeset svn:10160 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10160_date_2026_05_21_time_18_08_21_issue_117_set_records_create.sql
--

--
CREATE TABLE IF NOT EXISTS core.set_records (
    id            UUID  PRIMARY KEY             NOT NULL    DEFAULT pg_catalog.uuidv7(),
      CONSTRAINT  FK_77f3_core_set_records_base_records_id
      FOREIGN KEY (id)
      REFERENCES  core.base_records (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    title         VARCHAR(4096),
    texts         TEXT[]                        NOT NULL,
    user_name     VARCHAR(64)                   NOT NULL,
      CONSTRAINT  FK_4a13_core_set_records_security_user_name
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
--rollback DROP TABLE IF EXISTS core.set_records;
