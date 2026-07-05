--liquibase formatted sql
--

--
--changeset svn:10028 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10028_date_2026_04_02_time_10_47_51_issue_73_records_triggers.sql
--

--

DROP TRIGGER IF EXISTS update_create_time_api_post_records ON api.post_records;
CREATE TRIGGER update_create_time_api_post_records
    BEFORE INSERT
    ON api.post_records
    FOR EACH ROW
EXECUTE FUNCTION api.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_api_post_records ON api.post_records;
CREATE TRIGGER update_update_time_api_post_records
    BEFORE UPDATE
    ON api.post_records
    FOR EACH ROW
EXECUTE FUNCTION api.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_api_post_records ON api.post_records;
--rollback DROP TRIGGER IF EXISTS update_create_time_api_post_records ON api.post_records;
