--liquibase formatted sql
--

--
--changeset svn:10101 endDelimiter:$$ splitStatements:false failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10101_date_2026_03_26_time_10_00_00_issue_70_set_parent_if_null.sql
--

--

CREATE OR REPLACE FUNCTION core.set_parent_if_null() RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.parent_id IS NULL THEN
       NEW.parent_id = NEW.id;
    END IF;
    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION core.old_parent_if_null() RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.parent_id IS NULL THEN
       NEW.parent_id = OLD.parent_id;
    END IF;
    RETURN NEW;
END;
$$;

--
--rollback DROP FUNCTION IF EXISTS core.old_parent_if_null();
--rollback DROP FUNCTION IF EXISTS core.set_parent_if_null();
