--liquibase formatted sql
--

--
--changeset svn:10212 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10212_date_2026_05_25_time_11_56_42_issue_123_xml_records_create.sql
--

--
CREATE TABLE IF NOT EXISTS core.xml_records (
    id            UUID  PRIMARY KEY             NOT NULL    DEFAULT pg_catalog.uuidv7(),
      CONSTRAINT  FK_7482_core_xml_records_base_records_id
      FOREIGN KEY (id)
      REFERENCES  core.base_records (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    title         VARCHAR(4096),
    xml           XML                           NOT NULL,
    type          SMALLINT                      NOT NULL    DEFAULT 0      CHECK (0 <= type AND type <= 4),
    user_name     VARCHAR(64)                   NOT NULL,
      CONSTRAINT  FK_a237_core_xml_records_security_user_name
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
--rollback DROP TABLE IF EXISTS core.xml_records;
