--liquibase formatted sql
--

--
--changeset svn:10192 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10192_date_2026_05_22_time_19_01_50_issue_119_update_text_records_type_check.sql
--

--
ALTER TABLE core.text_records DROP CONSTRAINT text_records_type_check;
ALTER TABLE core.text_records ADD CONSTRAINT CK_7dcb_text_records_type_check CHECK (0 <= type AND type <= 4);

--
--rollback ALTER TABLE core.text_records DROP CONSTRAINT CK_7dcb_text_records_type_check;
--rollback ALTER TABLE core.text_records ADD CONSTRAINT text_records_type_check CHECK (type >= 0 AND type <= 3);
