--liquibase formatted sql
--

--
--changeset svn:10236 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10236_date_2026_06_11_time_09_47_45_issue_142_add_ahref_field.sql
--

--
ALTER TABLE core.base_records ADD ahref VARCHAR(4096);

--
--rollback ALTER TABLE core.base_records DROP IF EXISTS ahref;

