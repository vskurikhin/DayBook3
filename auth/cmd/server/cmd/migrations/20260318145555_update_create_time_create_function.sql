-- +goose Up
SELECT 'up SQL query update_create_time create function';
-- +goose StatementBegin
CREATE OR REPLACE FUNCTION update_create_time() RETURNS TRIGGER LANGUAGE plpgsql
AS $$
BEGIN
    NEW.create_time = now();
    NEW.update_time = NULL;
RETURN NEW;
END;
$$;
-- +goose StatementEnd

-- +goose Down
SELECT 'down SQL query update_create_time function drop';
DROP FUNCTION IF EXISTS update_create_time();