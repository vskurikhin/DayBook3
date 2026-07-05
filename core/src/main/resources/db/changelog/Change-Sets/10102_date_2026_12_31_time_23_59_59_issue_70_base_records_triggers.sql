--liquibase formatted sql
--

--
--changeset svn:10102 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/db/Change-Sets/10102_date_2026_12_31_time_23_59_59_issue_70_base_records_triggers.sql
--

--
DROP TRIGGER IF EXISTS update_create_time_core_base_records ON core.base_records;
CREATE TRIGGER update_create_time_core_base_records
    BEFORE INSERT
    ON core.base_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_core_base_records ON core.base_records;
CREATE TRIGGER update_update_time_core_base_records
    BEFORE UPDATE
    ON core.base_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_update_time();

DROP TRIGGER IF EXISTS set_parent_if_null_core_base_records ON core.base_records;
CREATE TRIGGER set_parent_if_null_core_base_records
    BEFORE INSERT
    ON core.base_records
    FOR EACH ROW
EXECUTE FUNCTION core.set_parent_if_null();

DROP TRIGGER IF EXISTS old_parent_if_null_core_base_records ON core.base_records;
CREATE TRIGGER old_parent_if_null_core_base_records
    BEFORE UPDATE
    ON core.base_records
    FOR EACH ROW
EXECUTE FUNCTION core.old_parent_if_null();

--
--rollback DROP TRIGGER IF EXISTS old_parent_if_null_core_base_records ON core.base_records;
--rollback DROP TRIGGER IF EXISTS set_parent_if_null_core_base_records ON core.base_records;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_base_records ON core.base_records;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_base_records ON core.base_records;
