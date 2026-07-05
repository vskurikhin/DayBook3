--liquibase formatted sql
--

--
--changeset svn:10104 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10105_date_2026_03_26_time_10_00_00_issue_1_test_data.sql
--

--
CREATE INDEX IF NOT EXISTS IDX_core_base_records_user_name
    ON core.base_records (user_name);

CREATE INDEX IF NOT EXISTS IDX_core_base_records_post_at
    ON core.base_records (post_at);

CREATE INDEX IF NOT EXISTS IDX_core_base_records_refresh_at
    ON core.base_records (refresh_at);

--
--rollback DROP INDEX IF EXISTS core.IDX_core_base_records_refresh_at;
--rollback DROP INDEX IF EXISTS core.IDX_core_base_records_post_at;
--rollback DROP INDEX IF EXISTS core.IDX_core_base_records_user_name;
