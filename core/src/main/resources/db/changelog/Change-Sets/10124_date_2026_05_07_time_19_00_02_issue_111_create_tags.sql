--liquibase formatted sql
--

--
--changeset svn:10124 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10124_date_2026_05_07_time_19_00_02_issue_111_create_tags.sql
--

--
CREATE TABLE IF NOT EXISTS core.tags (
    id            UUID  PRIMARY KEY             NOT NULL    DEFAULT pg_catalog.uuidv7(),
    tag           VARCHAR(32)                   NOT NULL
      CONSTRAINT  core_tag_must_be_unique       UNIQUE,
    user_name     VARCHAR(64)                   NOT NULL,
      CONSTRAINT  FK_bd07_core_tags_core_user_name
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
--rollback DROP TABLE IF EXISTS core.tags;
