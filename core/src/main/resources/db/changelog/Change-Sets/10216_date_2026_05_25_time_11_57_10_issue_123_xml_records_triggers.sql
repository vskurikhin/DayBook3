--liquibase formatted sql
--

--
--changeset svn:10216 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10216_date_2026_05_25_time_11_57_10_issue_123_xml_records_triggers.sql
--

--
DROP TRIGGER IF EXISTS update_create_time_core_xml_records ON core.xml_records;
CREATE TRIGGER update_create_time_core_xml_records
    BEFORE INSERT
    ON core.xml_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_create_time();

DROP TRIGGER IF EXISTS update_update_time_core_xml_records ON core.xml_records;
CREATE TRIGGER update_update_time_core_xml_records
    BEFORE UPDATE
    ON core.xml_records
    FOR EACH ROW
EXECUTE FUNCTION core.update_update_time();

--
--rollback DROP TRIGGER IF EXISTS update_update_time_core_xml_records ON core.xml_records;
--rollback DROP TRIGGER IF EXISTS update_create_time_core_xml_records ON core.xml_records;
