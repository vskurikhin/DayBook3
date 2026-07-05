--liquibase formatted sql
--

--
--changeset svn:10204 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10204_date_2026_05_24_time_11_30_59_issue_121_vector_records_idx.sql
--

--
CREATE INDEX IF NOT EXISTS IDX_core_vector_records_user_name
    ON core.vector_records (user_name);

--
--rollback DROP INDEX IF EXISTS core.IDX_core_vector_records_user_name;
