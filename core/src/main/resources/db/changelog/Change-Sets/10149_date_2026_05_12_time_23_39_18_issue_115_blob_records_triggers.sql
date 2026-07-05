--liquibase formatted sql
--

--
--changeset svn:10149 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10149_date_2026_05_12_time_23_39_18_issue_115_blob_records_triggers.sql
--

--
DROP TRIGGER IF EXISTS update_create_time_core_blob_records ON core.blob_records;
CREATE TRIGGER update_create_time_core_blob_records
    BEFORE INSERT
    ON core.blob_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_core_blob_records ON core.blob_records;
CREATE TRIGGER update_update_time_core_blob_records
    BEFORE UPDATE
    ON core.blob_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_core_blob_records ON core.blob_records;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_blob_records ON core.blob_records;
