--liquibase formatted sql
--

--
--changeset svn:10156 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10156_date_2026_05_21_time_10_02_40_issue_115_recreate_core_records_view.sql
--

--
DROP VIEW IF EXISTS core.records_view;
CREATE OR REPLACE VIEW core.records_view AS
SELECT  Br.*,
        COALESCE(Br.update_time, Br.create_time) last_changed_time,
        COALESCE(Bl.title, Jr.title) coalesce_title,
        Jr.json json,
        Bl.blob blob,
        array_agg(tags.tag ORDER BY tags.tag) tags
FROM core.base_records Br
    LEFT JOIN core.blob_records Bl ON Br.id = Bl.id AND Br.type = 1 -- RecordType.Blob
    LEFT JOIN core.json_records Jr ON Br.id = Jr.id AND Br.type = 2 -- RecordType.Json
    LEFT JOIN core.base_records_has_tags brht ON Br.id = brht.base_records_id
    LEFT JOIN core.tags ON brht.tag_id = core.tags.id
    GROUP BY Br.id, last_changed_time, coalesce_title, blob, json;

--
--rollback DROP VIEW IF EXISTS core.records_view;
--rollback CREATE OR REPLACE VIEW core.records_view AS SELECT  Br.*, COALESCE(Br.update_time, Br.create_time) last_changed_time, COALESCE(Jr.title) title, Jr.json json, array_agg(tags.tag ORDER BY tags.tag) tags FROM core.base_records Br LEFT JOIN core.json_records Jr ON Br.id = Jr.id AND Br.type = 5 LEFT JOIN core.base_records_has_tags brht ON Br.id = brht.base_records_id LEFT JOIN core.tags ON brht.tag_id = core.tags.id GROUP BY Br.id, last_changed_time, title, json;