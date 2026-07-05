--liquibase formatted sql
--

--
--changeset svn:10000 failOnError:true logicalFilePath:src/main/resources/db/changelog/db/Change-Sets/10000_date_2026_04_01_time_15_15_00_issue_73_schema.sql
--

--
--  NOT-rollback DROP SCHEMA IF EXISTS api CASCADE;
CREATE SCHEMA IF NOT EXISTS api;

--
--rollback SELECT 1;