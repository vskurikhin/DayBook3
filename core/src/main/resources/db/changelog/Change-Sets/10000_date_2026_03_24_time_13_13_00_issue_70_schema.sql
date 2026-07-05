--liquibase formatted sql
--

--
--changeset svn:10000 failOnError:true logicalFilePath:src/main/resources/db/changelog/db/Change-Sets/10000_date_2026_03_24_time_13_13_00_issue_70_schema.sql
--

--
--  NOT-rollback DROP SCHEMA IF EXISTS core CASCADE;
CREATE SCHEMA IF NOT EXISTS core;

--
--rollback SELECT 1;