--liquibase formatted sql
--

--
--changeset svn:10012 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/db/Change-Sets/10012_date_2026_03_26_time_08_35_00_issue_70_user_name_triggers.sql
--

--
DROP TRIGGER IF EXISTS update_create_time_core_user_name ON core.user_name;
CREATE TRIGGER update_create_time_core_user_name
    BEFORE INSERT
    ON core.user_name
    FOR EACH ROW
    EXECUTE FUNCTION core.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_core_user_name ON core.user_name;
CREATE TRIGGER update_update_time_core_user_name
    BEFORE UPDATE
    ON core.user_name
    FOR EACH ROW
    EXECUTE FUNCTION core.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_core_user_name ON core.user_name;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_user_name ON core.user_name;