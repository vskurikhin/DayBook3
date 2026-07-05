--liquibase formatted sql
--

--
--changeset svn:10042 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10042_date_2026_05_21_time_11_18_32_issue_115_api_post_records_add_column_blob.sql
--

--
ALTER TABLE api.post_records ADD COLUMN IF NOT EXISTS blob BYTEA;

--
--rollback ALTER TABLE api.post_records DROP COLUMN IF EXISTS blob;
