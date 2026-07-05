--liquibase formatted sql
--

--
--changeset svn:10232 endDelimiter:; failOnError:true logicalFilePath:src/main/resources/db/changelog/Change-Sets/10232_date_2026_06_10_time_10_21_02_issue_137_refactor_title_field.sql
--

--
DROP VIEW IF EXISTS core.records_view;

ALTER TABLE core.base_records ADD title VARCHAR(4096);

ALTER TABLE core.blob_records DROP IF EXISTS title;
ALTER TABLE core.json_records DROP IF EXISTS title;
ALTER TABLE core.set_records DROP IF EXISTS title;
ALTER TABLE core.text_records DROP IF EXISTS title;
ALTER TABLE core.vector_records DROP IF EXISTS title;
ALTER TABLE core.xml_records DROP IF EXISTS title;

CREATE OR REPLACE VIEW core.records_view AS
SELECT  Br.*,
        COALESCE(Br.update_time, Br.create_time) last_changed_time,
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
                 blob, json, texts, file_name, html, link, markdown, value, vector, xr.id;

--
--rollback DROP VIEW IF EXISTS core.records_view;
--rollback ALTER TABLE core.xml_records ADD title VARCHAR(4096);
--rollback ALTER TABLE core.vector_records ADD title VARCHAR(4096);
--rollback ALTER TABLE core.text_records ADD title VARCHAR(4096);
--rollback ALTER TABLE core.set_records ADD title VARCHAR(4096);
--rollback ALTER TABLE core.json_records ADD title VARCHAR(4096);
--rollback ALTER TABLE core.blob_records ADD title VARCHAR(4096);
--rollback ALTER TABLE core.base_records DROP IF EXISTS title;
--rollback CREATE OR REPLACE VIEW core.records_view AS
--rollback SELECT Br.*,
--rollback        COALESCE(Br.update_time, Br.create_time) last_changed_time,
--rollback        COALESCE(Bl.title, Jr.title, Sr.title, Tr.title, Vr.title, Xr.title)::VARCHAR(4096) coalesce_title,
--rollback        Jr.json json,
--rollback        Bl.blob blob,
--rollback        Sr.texts texts,
--rollback        Tr.file_name file_name,
--rollback        Tr.html html,
--rollback        Tr.link link,
--rollback        Tr.markdown markdown,
--rollback        Tr.value value,
--rollback        Vr.vector vector,
--rollback        Xr.xml xml,
--rollback        CASE
--rollback            WHEN COUNT(tags.tag) = 0 THEN NULL
--rollback            ELSE array_agg(tags.tag ORDER BY tags.tag)
--rollback        END AS tags
--rollback FROM core.base_records Br
--rollback    LEFT JOIN core.blob_records Bl ON Br.id = Bl.id AND Br.type = 1
--rollback    LEFT JOIN core.json_records Jr ON Br.id = Jr.id AND Br.type = 2
--rollback    LEFT JOIN core.set_records Sr ON Br.id = Sr.id AND Br.type = 3
--rollback    LEFT JOIN LATERAL(
--rollback        SELECT title, type AS text_type,
--rollback            CASE
--rollback                WHEN type = 0 THEN value
--rollback            END AS value,
--rollback            CASE
--rollback                WHEN type = 1 THEN value
--rollback            END AS file_name,
--rollback            CASE
--rollback                WHEN type = 2 THEN value
--rollback            END AS html,
--rollback            CASE
--rollback                WHEN type = 3 THEN value
--rollback            END AS link,
--rollback            CASE
--rollback                WHEN type = 4 THEN value
--rollback            END AS markdown
--rollback        FROM core.text_records WHERE id = Br.id
--rollback    ) Tr ON TRUE AND Br.type = 4 -- RecordType.Text
--rollback    LEFT JOIN core.vector_records Vr ON Br.id = Vr.id AND Br.type = 5 -- RecordType.Vector
--rollback    LEFT JOIN core.xml_records Xr ON Br.id = Xr.id AND Br.type = 6 -- RecordType.Xml
--rollback    LEFT JOIN core.base_records_has_tags brht ON Br.id = brht.base_records_id
--rollback    LEFT JOIN core.tags ON brht.tag_id = core.tags.id
--rollback    GROUP BY Br.id, COALESCE(Br.update_time, Br.create_time),
--rollback                 COALESCE(Bl.title, Jr.title, Sr.title, Tr.title, Vr.title, Xr.title),
--rollback                 blob, json, texts, file_name, html, link, markdown, value, vector, xr.id;
