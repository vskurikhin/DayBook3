--liquibase formatted sql
--

--
--changeset svn:10054 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10054_date_2026_05_24_time_21_37_13_issue_121_api_post_records_add_column_vector.sql
--

--
ALTER TABLE api.post_records ADD COLUMN IF NOT EXISTS vector JSONB;

--
--rollback ALTER TABLE api.post_records DROP COLUMN IF EXISTS vector;
