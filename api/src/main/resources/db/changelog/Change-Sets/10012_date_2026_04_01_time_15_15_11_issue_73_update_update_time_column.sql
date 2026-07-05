--liquibase formatted sql
--

--
--changeset svn:10012 endDelimiter:$$ splitStatements:false failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10012_date_2026_04_01_time_15_15_11_issue_73_update_update_time_column.sql
--

--
CREATE OR REPLACE FUNCTION api.update_update_time() RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    NEW.create_time = OLD.create_time;
    NEW.update_time = now();
    RETURN NEW;
END;
$$;

--
--rollback DROP FUNCTION IF EXISTS api.update_update_time();
