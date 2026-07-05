--liquibase formatted sql
--

--
--changeset svn:10112 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10112_date_2026_03_26_time_10_00_00_issue_70_records_view_create.sql
--

--
DROP VIEW IF EXISTS core.records_view;
CREATE OR REPLACE VIEW core.records_view AS
SELECT Br.*, COALESCE(Br.update_time, Br.create_time) last_changed_time, Jr.title, Jr.values
FROM core.base_records Br
    LEFT JOIN core.json_records Jr ON Br.id = Jr.id;

--
--rollback DROP VIEW IF EXISTS core.records_view;
