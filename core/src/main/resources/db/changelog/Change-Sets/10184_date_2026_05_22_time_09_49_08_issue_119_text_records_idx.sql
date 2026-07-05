--liquibase formatted sql
--

--
--changeset svn:10184 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10184_date_2026_05_22_time_09_49_08_issue_119_text_records_idx.sql
--

--
CREATE INDEX IF NOT EXISTS IDX_core_text_records_user_name
    ON core.text_records (user_name);

--
--rollback DROP INDEX IF EXISTS core.IDX_core_text_records_user_name;
