--liquibase formatted sql
--

--
--changeset svn:10062 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10062_date_2026_06_11_time_12_42_17_issue_142_add_ahref_field.sql
--

--
ALTER TABLE api.post_records ADD ahref VARCHAR(4096);

--
--rollback ALTER TABLE api.post_records DROP IF EXISTS ahref;