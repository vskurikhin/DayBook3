--liquibase formatted sql
--

--
--changeset svn:10038 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10038_date_2026_05_08_time_18_28_49_issue_113_modify_post_records_type_check_constraint.sql
--

--
ALTER TABLE api.post_records DROP CONSTRAINT post_records_type_check;
ALTER TABLE api.post_records ADD CONSTRAINT CK_7d41_post_records_type_check CHECK (0 <= type AND type <= 11);

--
--rollback ALTER TABLE api.post_records DROP CONSTRAINT CK_7d41_post_records_type_check;
--rollback ALTER TABLE api.post_records ADD CONSTRAINT post_records_type_check CHECK (type >= 0 AND type <= 7);
