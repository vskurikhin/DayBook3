--liquibase formatted sql
--

--
--changeset svn:10108 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10108_date_2026_03_26_time_10_00_00_issue_70_json_records_triggers.sql
--

--
DROP TRIGGER IF EXISTS update_create_time_core_json_records ON core.json_records;
CREATE TRIGGER update_create_time_core_json_records
    BEFORE INSERT
    ON core.json_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_core_json_records ON core.json_records;
CREATE TRIGGER update_update_time_core_json_records
    BEFORE UPDATE
    ON core.json_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_core_json_records ON core.json_records;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_json_records ON core.json_records;
