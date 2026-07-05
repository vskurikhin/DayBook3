--liquibase formatted sql
--

--
--changeset svn:10046 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10046_date_2026_05_21_time_23_40_36_issue_117_api_post_records_add_column_texts.sql
--

--
ALTER TABLE api.post_records ADD COLUMN IF NOT EXISTS texts TEXT[];

--
--rollback ALTER TABLE api.post_records DROP COLUMN IF EXISTS texts;
