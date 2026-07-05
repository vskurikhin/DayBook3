--liquibase formatted sql
--

--
--changeset svn:10008 endDelimiter:$$ splitStatements:false failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10008_date_2026_04_01_time_15_15_06_issue_73_update_create_time_column.sql
--

--
CREATE OR REPLACE FUNCTION api.update_create_time() RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    NEW.create_time = now();
    NEW.update_time = NULL;
    RETURN NEW;
END;
$$;

--
--rollback DROP FUNCTION IF EXISTS api.update_create_time();
