--liquibase formatted sql
--

--
--changeset svn:10020 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10020_date_2026_04_01_time_16_47_23_issue_73_user_name_triggers.sql
--

--

DROP TRIGGER IF EXISTS update_create_time_api_user_name ON api.user_name;
CREATE TRIGGER update_create_time_api_user_name
    BEFORE INSERT
    ON api.user_name
    FOR EACH ROW
    EXECUTE FUNCTION api.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_api_user_name ON api.user_name;
CREATE TRIGGER update_update_time_api_user_name
    BEFORE UPDATE
    ON api.user_name
    FOR EACH ROW
    EXECUTE FUNCTION api.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_api_user_name ON api.user_name;
--rollback DROP TRIGGER IF EXISTS update_create_time_api_user_name ON api.user_name;
