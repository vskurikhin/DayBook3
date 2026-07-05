--liquibase formatted sql
--

--
--changeset svn:10200 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10200_date_2026_05_24_time_11_30_29_issue_121_vector_records_triggers.sql
--

--
DROP TRIGGER IF EXISTS update_create_time_core_vector_records ON core.vector_records;
CREATE TRIGGER update_create_time_core_vector_records
    BEFORE INSERT
    ON core.vector_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_core_vector_records ON core.vector_records;
CREATE TRIGGER update_update_time_core_vector_records
    BEFORE UPDATE
    ON core.vector_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_core_vector_records ON core.vector_records;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_vector_records ON core.vector_records;
