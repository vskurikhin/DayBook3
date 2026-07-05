--liquibase formatted sql
--

--
--changeset svn:10002 failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10002_date_2026_03_24_time_13_15_00_issue_70_create_extension_vector.sql
--

--
CREATE EXTENSION IF NOT EXISTS vector SCHEMA pg_catalog;
--

--
--rollback DROP EXTENSION IF EXISTS vector;