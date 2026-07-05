--liquibase formatted sql
--

--
--changeset svn:10013 endDelimiter:$$ splitStatements:false failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10013_date_2026_03_26_time_10_00_00_issue_70_set_id.sql
--

--

CREATE OR REPLACE FUNCTION core.forced_set_id_uuidv7() RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    NEW.id = pg_catalog.uuidv7();
    RETURN NEW;
END;
$$;

--
--rollback DROP FUNCTION IF EXISTS core.forced_set_id_uuidv7();
