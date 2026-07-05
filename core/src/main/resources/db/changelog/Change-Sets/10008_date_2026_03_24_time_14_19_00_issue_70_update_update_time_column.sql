--liquibase formatted sql
--

--
--changeset svn:10008 endDelimiter:$$ splitStatements:false failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10008_date_2026_03_24_time_14_19_00_issue_70_update_update_time_column.sql
--

--
CREATE OR REPLACE FUNCTION core.update_update_time() RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    NEW.create_time = OLD.create_time;
    NEW.update_time = now();
    RETURN NEW;
END;
$$;
--
--rollback DROP FUNCTION IF EXISTS core.update_update_time();
