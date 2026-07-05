--liquibase formatted sql
--

--
--changeset svn:10050 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10050_date_2026_05_22_time_14_07_50_issue_119_api_post_records_add_columns_file_name_html_link_markdown_value.sql
--

--
ALTER TABLE api.post_records ADD COLUMN IF NOT EXISTS file_name VARCHAR(255);
ALTER TABLE api.post_records ADD COLUMN IF NOT EXISTS html VARCHAR(65535);
ALTER TABLE api.post_records ADD COLUMN IF NOT EXISTS link VARCHAR(255);
ALTER TABLE api.post_records ADD COLUMN IF NOT EXISTS markdown VARCHAR(65535);
ALTER TABLE api.post_records ADD COLUMN IF NOT EXISTS value VARCHAR(4096);

--
--rollback ALTER TABLE api.post_records DROP COLUMN IF EXISTS value;
--rollback ALTER TABLE api.post_records DROP COLUMN IF EXISTS markdown;
--rollback ALTER TABLE api.post_records DROP COLUMN IF EXISTS link;
--rollback ALTER TABLE api.post_records DROP COLUMN IF EXISTS html;
--rollback ALTER TABLE api.post_records DROP COLUMN IF EXISTS file_name;
