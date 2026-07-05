--liquibase formatted sql
--

--
--changeset svn:10030 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10030_date_2026_05_03_time_15_28_07_issue_107_sequence_id_set_user_name_seq.sql
--

--
ALTER TABLE api.user_name ALTER COLUMN sequence_id SET DEFAULT nextval('api.user_name_seq');

--
--rollback ALTER TABLE api.user_name ALTER COLUMN sequence_id DROP DEFAULT;
