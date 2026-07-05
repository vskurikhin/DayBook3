--liquibase formatted sql
--

--
--changeset svn:10132 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10132_date_2026_05_07_time_19_17_06_issue_111_base_records_has_tags.sql
--

--
CREATE TABLE IF NOT EXISTS core.base_records_has_tags (
    base_records_id UUID                          NOT NULL,
      FOREIGN KEY   (base_records_id)
      REFERENCES    core.base_records (id)
        ON DELETE   CASCADE ON UPDATE CASCADE,
    tag_id          UUID                          NOT NULL,
      FOREIGN KEY   (tag_id)
      REFERENCES    core.tags (id)
        ON DELETE   CASCADE ON UPDATE CASCADE,
    create_time     TIMESTAMP WITHOUT TIME ZONE   NOT NULL    DEFAULT now(),
    update_time     TIMESTAMP WITHOUT TIME ZONE               DEFAULT now(),
    enabled         BOOLEAN                                   DEFAULT true,
    PRIMARY KEY (base_records_id, tag_id)
    );

--
--rollback DROP TABLE IF EXISTS core.base_records_has_tags;
