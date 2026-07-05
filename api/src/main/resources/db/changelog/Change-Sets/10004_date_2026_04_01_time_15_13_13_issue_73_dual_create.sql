--liquibase formatted sql
--

--
--changeset svn:10004 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10004_date_2026_04_01_time_15_13_13_issue_73_dual_create.sql
--

--
CREATE TABLE IF NOT EXISTS api.dual AS ( VALUES (true) );

--
--rollback DROP TABLE IF EXISTS api.dual;
