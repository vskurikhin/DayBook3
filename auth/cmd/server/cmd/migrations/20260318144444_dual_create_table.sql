-- +goose Up
SELECT 'up SQL query dual create table';
CREATE TABLE IF NOT EXISTS dual AS ( VALUES (true) );

-- +goose Down
SELECT 'down SQL query dual table drop';
DROP TABLE IF EXISTS dual;
