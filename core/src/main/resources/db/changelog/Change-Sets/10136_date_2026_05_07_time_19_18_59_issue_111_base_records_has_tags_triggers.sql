--liquibase formatted sql
--

--
--changeset svn:10136 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10136_date_2026_05_07_time_19_18_59_issue_111_base_records_has_base_records_has_tags_triggers.sql
--

--
DROP TRIGGER IF EXISTS update_create_time_core_base_records_has_tags ON core.base_records_has_tags;
CREATE TRIGGER update_create_time_core_base_records_has_tags
    BEFORE INSERT
    ON core.base_records_has_tags
    FOR EACH ROW
EXECUTE FUNCTION core.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_core_base_records_has_tags ON core.base_records_has_tags;
CREATE TRIGGER update_update_time_core_base_records_has_tags
    BEFORE UPDATE
    ON core.base_records_has_tags
    FOR EACH ROW
EXECUTE FUNCTION core.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_core_base_records_has_tags ON core.base_records_has_tags;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_base_records_has_tags ON core.base_records_has_tags;
