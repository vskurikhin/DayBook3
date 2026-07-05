--liquibase formatted sql
--

--
--changeset svn:10004 endDelimiter:$$ splitStatements:false failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10004_date_2026_03_24_time_14_14_13_issue_70_update_create_time_column.sql
--

--
CREATE OR REPLACE FUNCTION core.update_create_time() RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    NEW.create_time = now();
    NEW.update_time = NULL;
    RETURN NEW;
END;
$$;

--
--rollback DROP FUNCTION IF EXISTS core.update_create_time();
