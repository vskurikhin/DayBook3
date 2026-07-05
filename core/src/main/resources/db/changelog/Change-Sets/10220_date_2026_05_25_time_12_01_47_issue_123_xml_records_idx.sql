--liquibase formatted sql
--

--
--changeset svn:10220 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10220_date_2026_05_25_time_12_01_47_issue_123_xml_records_idx.sql
--

--
CREATE INDEX IF NOT EXISTS IDX_core_xml_records_user_name
    ON core.xml_records (user_name);

--
--rollback DROP INDEX IF EXISTS core.IDX_core_xml_records_user_name;
