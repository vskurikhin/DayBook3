--liquibase formatted sql
--

--
--changeset svn:10128 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10132_date_2026_05_07_time_19_17_06_issue_111_create_base_records_has_tags.sql
--

--
DROP TRIGGER IF EXISTS update_create_time_core_tags ON core.tags;
CREATE TRIGGER update_create_time_core_tags
    BEFORE INSERT
    ON core.tags
    FOR EACH ROW
EXECUTE FUNCTION core.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_core_tags ON core.tags;
CREATE TRIGGER update_update_time_core_tags
    BEFORE UPDATE
    ON core.tags
    FOR EACH ROW
EXECUTE FUNCTION core.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_core_tags ON core.tags;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_tags ON core.tags;
