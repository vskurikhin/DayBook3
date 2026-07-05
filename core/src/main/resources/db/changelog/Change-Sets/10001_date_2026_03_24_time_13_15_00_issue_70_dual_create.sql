--liquibase formatted sql
--

--
--changeset svn:10001 failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10001_date_2026_03_24_time_13_15_00_issue_70_dual_create.sql
--

--
CREATE TABLE IF NOT EXISTS core.dual AS ( VALUES (true) );
--

--
--rollback DROP TABLE IF EXISTS core.dual;