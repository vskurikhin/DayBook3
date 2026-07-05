--liquibase formatted sql
--

--
--changeset svn:10144 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10144_date_2026_05_08_time_18_28_17_issue_113_modify_base_records_type_check_constraint.sql
--

--
ALTER TABLE core.base_records DROP CONSTRAINT base_records_type_check;
ALTER TABLE core.base_records ADD CONSTRAINT CK_7bef_post_records_type_check CHECK (0 <= type AND type <= 11);

--
--rollback ALTER TABLE core.base_records DROP CONSTRAINT CK_7bef_post_records_type_check;
--rollback ALTER TABLE core.base_records ADD CONSTRAINT base_records_type_check CHECK (type >= 0 AND type <= 7);
