--liquibase formatted sql
--

--
--changeset svn:10164 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10164_date_2026_05_21_time_18_42_25_issue_117_set_records_triggers.sql
--

--
DROP TRIGGER IF EXISTS update_create_time_core_set_records ON core.set_records;
CREATE TRIGGER update_create_time_core_set_records
    BEFORE INSERT
    ON core.set_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_core_set_records ON core.set_records;
CREATE TRIGGER update_update_time_core_set_records
    BEFORE UPDATE
    ON core.set_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_core_set_records ON core.set_records;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_set_records ON core.set_records;
