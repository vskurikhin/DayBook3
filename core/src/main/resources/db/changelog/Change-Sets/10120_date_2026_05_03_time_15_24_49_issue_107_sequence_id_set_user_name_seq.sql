--liquibase formatted sql
--

--
--changeset svn:10120 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10120_date_2026_05_03_time_15_24_49_issue_107_sequence_id_set_user_name_seq.sql
--

--
ALTER TABLE core.user_name ALTER COLUMN sequence_id SET DEFAULT nextval('core.user_name_seq');

--
--rollback ALTER TABLE core.user_name ALTER COLUMN sequence_id DROP DEFAULT;
