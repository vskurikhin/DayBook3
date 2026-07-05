--liquibase formatted sql
--

--
--changeset svn:10172 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10172_date_2026_05_21_time_18_54_40_issue_117_recreate_core_records_view.sql
--

--
DROP VIEW IF EXISTS core.records_view;
CREATE OR REPLACE VIEW core.records_view AS
SELECT  Br.*,
        COALESCE(Br.update_time, Br.create_time) last_changed_time,
        COALESCE(Bl.title, Jr.title, Sr.title) coalesce_title,
        Jr.json json,
        Bl.blob blob,
        Sr.texts texts,
        array_agg(tags.tag ORDER BY tags.tag) tags
FROM core.base_records Br
    LEFT JOIN core.blob_records Bl ON Br.id = Bl.id AND Br.type = 1 -- RecordType.Blob
    LEFT JOIN core.json_records Jr ON Br.id = Jr.id AND Br.type = 2 -- RecordType.Json
    LEFT JOIN core.set_records Sr ON Br.id = Sr.id AND Br.type = 3 -- RecordType.Set
    LEFT JOIN core.base_records_has_tags brht ON Br.id = brht.base_records_id
    LEFT JOIN core.tags ON brht.tag_id = core.tags.id
    GROUP BY Br.id, last_changed_time, coalesce_title, blob, json, texts;

--
--rollback DROP VIEW IF EXISTS core.records_view;
--rollback CREATE OR REPLACE VIEW core.records_view AS SELECT  Br.*, COALESCE(Br.update_time, Br.create_time) last_changed_time, COALESCE(Bl.title, Jr.title) coalesce_title, Jr.json json, Bl.blob blob, array_agg(tags.tag ORDER BY tags.tag) tags FROM core.base_records Br LEFT JOIN core.blob_records Bl ON Br.id = Bl.id AND Br.type = 1 LEFT JOIN core.json_records Jr ON Br.id = Jr.id AND Br.type = 2 LEFT JOIN core.base_records_has_tags brht ON Br.id = brht.base_records_id LEFT JOIN core.tags ON brht.tag_id = core.tags.id GROUP BY Br.id, last_changed_time, coalesce_title, blob, json;
