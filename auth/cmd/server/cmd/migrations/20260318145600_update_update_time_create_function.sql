-- +goose Up
SELECT 'up SQL query update_update_time create function';
-- +goose StatementBegin
CREATE OR REPLACE FUNCTION update_update_time() RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    NEW.create_time = OLD.create_time;
    NEW.update_time = now();
RETURN NEW;
END;
$$;
-- +goose StatementEnd

-- +goose Down
SELECT 'down SQL query update_update_time function drop';
DROP FUNCTION IF EXISTS update_update_time();
