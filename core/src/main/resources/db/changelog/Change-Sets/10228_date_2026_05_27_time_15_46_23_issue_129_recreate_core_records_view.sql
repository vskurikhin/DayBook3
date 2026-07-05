--liquibase formatted sql
--

--
--changeset svn:10228 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10228_date_2026_05_27_time_15_46_23_issue_129_recreate_core_records_view.sql
--

--
DROP VIEW IF EXISTS core.records_view;
CREATE OR REPLACE VIEW core.records_view AS
SELECT  Br.*,
        COALESCE(Br.update_time, Br.create_time) last_changed_time,
        COALESCE(Bl.title, Jr.title, Sr.title, Tr.title, Vr.title, Xr.title)::VARCHAR(4096) coalesce_title,
        Jr.json json,
        Bl.blob blob,
        Sr.texts texts,
        Tr.file_name file_name,
        Tr.html html,
        Tr.link link,
        Tr.markdown markdown,
        Tr.value value,
        Vr.vector vector,
        Xr.xml xml,
        CASE
            WHEN COUNT(tags.tag) = 0 THEN NULL
            ELSE array_agg(tags.tag ORDER BY tags.tag)
        END AS tags
FROM core.base_records Br
    LEFT JOIN core.blob_records Bl ON Br.id = Bl.id AND Br.type = 1 -- RecordType.Blob
    LEFT JOIN core.json_records Jr ON Br.id = Jr.id AND Br.type = 2 -- RecordType.Json
    LEFT JOIN core.set_records Sr ON Br.id = Sr.id AND Br.type = 3 -- RecordType.Set
    LEFT JOIN LATERAL(
        SELECT title, type AS text_type,
            CASE
                WHEN type = 0 THEN value
            END AS value,
            CASE
                WHEN type = 1 THEN value
            END AS file_name,
            CASE
                WHEN type = 2 THEN value
            END AS html,
            CASE
                WHEN type = 3 THEN value
            END AS link,
            CASE
                WHEN type = 4 THEN value
            END AS markdown
        FROM core.text_records WHERE id = Br.id
    ) Tr ON TRUE AND Br.type = 4 -- RecordType.Text
    LEFT JOIN core.vector_records Vr ON Br.id = Vr.id AND Br.type = 5 -- RecordType.Vector
    LEFT JOIN core.xml_records Xr ON Br.id = Xr.id AND Br.type = 6 -- RecordType.Xml
    LEFT JOIN core.base_records_has_tags brht ON Br.id = brht.base_records_id
    LEFT JOIN core.tags ON brht.tag_id = core.tags.id
    GROUP BY Br.id, COALESCE(Br.update_time, Br.create_time),
                 COALESCE(Bl.title, Jr.title, Sr.title, Tr.title, Vr.title, Xr.title),
                 blob, json, texts, file_name, html, link, markdown, value, vector, xr.id;

--
--rollback DROP VIEW IF EXISTS core.records_view;
--rollback CREATE OR REPLACE VIEW core.records_view AS SELECT  Br.*, COALESCE(Br.update_time, Br.create_time) last_changed_time, COALESCE(Bl.title, Jr.title, Sr.title, Tr.title, Vr.title, Xr.title) coalesce_title, Jr.json json, Bl.blob blob, Sr.texts texts, Tr.file_name file_name, Tr.html html, Tr.link link, Tr.markdown markdown, Tr.value value, Vr.vector vector, Xr.xml xml, array_agg(tags.tag ORDER BY tags.tag) tags FROM core.base_records Br LEFT JOIN core.blob_records Bl ON Br.id = Bl.id AND Br.type = 1 LEFT JOIN core.json_records Jr ON Br.id = Jr.id AND Br.type = 2 LEFT JOIN core.set_records Sr ON Br.id = Sr.id AND Br.type = 3 LEFT JOIN LATERAL(SELECT title, type AS text_type, CASE WHEN type = 0 THEN value END AS value, CASE WHEN type = 1 THEN value END AS file_name, CASE WHEN type = 2 THEN value END AS html, CASE WHEN type = 3 THEN value END AS link, CASE WHEN type = 4 THEN value END AS markdown FROM core.text_records WHERE id = Br.id) Tr ON TRUE AND Br.type = 4 LEFT JOIN core.vector_records Vr ON Br.id = Vr.id AND Br.type = 5  LEFT JOIN core.xml_records Xr ON Br.id = Xr.id AND Br.type = 6 LEFT JOIN core.base_records_has_tags brht ON Br.id = brht.base_records_id LEFT JOIN core.tags ON brht.tag_id = core.tags.id GROUP BY Br.id, last_changed_time, coalesce_title, blob, json, texts, file_name, html, link, markdown, value, vector, xr.id;
