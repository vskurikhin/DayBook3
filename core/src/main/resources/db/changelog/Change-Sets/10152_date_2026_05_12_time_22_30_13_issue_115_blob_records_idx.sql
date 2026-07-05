--liquibase formatted sql
--

--
--changeset svn:10152 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10152_date_2026_05_12_time_22_30_13_issue_115_blob_records_idx.sql
--

--
CREATE INDEX IF NOT EXISTS IDX_core_blob_records_user_name
    ON core.blob_records (user_name);

--
--rollback DROP INDEX IF EXISTS core.IDX_core_blob_records_user_name;
