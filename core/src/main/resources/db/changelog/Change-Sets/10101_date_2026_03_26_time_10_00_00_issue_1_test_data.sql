--liquibase formatted sql
--

--
--changeset svn:10101 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10101_date_2026_03_26_time_10_00_00_issue_1_test_data.sql
--

--
INSERT INTO core.user_name (user_name, id, sequence_id) VALUES ('root', '00000000-0000-0000-0000-000000000000', (SELECT nextval('core.user_name_seq')));
INSERT INTO core.base_records (id, parent_id, user_name, create_time, update_time)
 VALUES ('00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000000', 'root', to_timestamp(0)::timestamp, to_timestamp(0)::timestamp);

--
--rollback DELETE FROM core.base_records WHERE id = '00000000-0000-0000-0000-000000000000';
--rollback DELETE FROM core.user_name WHERE user_name = 'root';
