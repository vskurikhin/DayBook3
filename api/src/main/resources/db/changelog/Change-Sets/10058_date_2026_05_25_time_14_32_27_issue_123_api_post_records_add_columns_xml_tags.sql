--liquibase formatted sql
--

--
--changeset svn:10058 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10058_date_2026_05_25_time_14_32_27_issue_123_api_post_records_add_columns_xml_tags.sql
--

--
ALTER TABLE api.post_records ADD COLUMN IF NOT EXISTS xml VARCHAR(10485760);
ALTER TABLE api.post_records ADD COLUMN IF NOT EXISTS tags VARCHAR(32)[];

--
--rollback ALTER TABLE api.post_records DROP COLUMN IF EXISTS tags;
--rollback ALTER TABLE api.post_records DROP COLUMN IF EXISTS xml;
