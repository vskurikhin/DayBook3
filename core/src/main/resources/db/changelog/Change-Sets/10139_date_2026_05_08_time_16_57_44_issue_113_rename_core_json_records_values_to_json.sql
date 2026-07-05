--liquibase formatted sql
--

--
--changeset svn:10139 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10139_date_2026_05_08_time_16_57_44_issue_113_rename_core_json_records_values_to_json.sql
--

--
ALTER TABLE core.json_records RENAME COLUMN values TO json;
ALTER TABLE core.json_records ALTER COLUMN json SET NOT NULL;

DROP VIEW IF EXISTS core.records_view;

CREATE OR REPLACE VIEW core.records_view AS
SELECT  Br.*,
        COALESCE(Br.update_time, Br.create_time) last_changed_time,
        COALESCE(Jr.title) title,
        Jr.json json,
        array_agg(tags.tag ORDER BY tags.tag) tags
FROM core.base_records Br
    LEFT JOIN core.json_records Jr ON Br.id = Jr.id AND Br.type = 5 -- RecordType.Json
    LEFT JOIN core.base_records_has_tags brht ON Br.id = brht.base_records_id
    LEFT JOIN core.tags ON brht.tag_id = core.tags.id
    GROUP BY Br.id, last_changed_time, title, json;

--
--rollback ALTER TABLE core.json_records ALTER COLUMN json DROP NOT NULL;
--rollback ALTER TABLE core.json_records RENAME COLUMN json TO values;
--rollback DROP VIEW IF EXISTS core.records_view;
--rollback CREATE OR REPLACE VIEW core.records_view AS SELECT Br.*, COALESCE(Br.update_time, Br.create_time) last_changed_time, Jr.title, Jr.values FROM core.base_records Br LEFT JOIN core.json_records Jr ON Br.id = Jr.id;
