--liquibase formatted sql
--

--
--changeset svn:10168 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10168_date_2026_05_21_time_18_44_19_issue_117_set_records_idx.sql
--

--
CREATE INDEX IF NOT EXISTS IDX_core_set_records_user_name
    ON core.set_records (user_name);

--
--rollback DROP INDEX IF EXISTS core.IDX_core_set_records_user_name;
