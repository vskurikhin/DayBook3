--liquibase formatted sql
--

--
--changeset svn:10034 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10034_date_2026_05_08_time_18_20_28_issue_113_rename_api_post_records_values_to_json.sql
--

--
ALTER TABLE api.post_records RENAME COLUMN values TO json;

--
--rollback ALTER TABLE api.post_records RENAME COLUMN json TO values;
