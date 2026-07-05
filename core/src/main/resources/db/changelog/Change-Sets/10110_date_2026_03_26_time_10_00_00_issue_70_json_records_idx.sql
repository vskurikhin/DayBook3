--liquibase formatted sql
--

--
--changeset svn:10110 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10110_date_2026_03_26_time_10_00_00_issue_70_json_records_idx.sql
--

--
CREATE INDEX IF NOT EXISTS IDX_core_json_records_user_name
    ON core.json_records (user_name);

--
--rollback DROP INDEX IF EXISTS core.IDX_core_json_records_user_name;
