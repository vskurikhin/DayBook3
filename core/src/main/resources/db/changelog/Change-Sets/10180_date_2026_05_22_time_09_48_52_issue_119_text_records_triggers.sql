--liquibase formatted sql
--

--
--changeset svn:10180 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10180_date_2026_05_22_time_09_48_52_issue_119_text_records_triggers.sql
--

--
DROP TRIGGER IF EXISTS update_create_time_core_text_records ON core.text_records;
CREATE TRIGGER update_create_time_core_text_records
    BEFORE INSERT
    ON core.text_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_core_text_records ON core.text_records;
CREATE TRIGGER update_update_time_core_text_records
    BEFORE UPDATE
    ON core.text_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_core_text_records ON core.text_records;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_text_records ON core.text_records;
