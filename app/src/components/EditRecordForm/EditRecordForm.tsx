import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { marked } from "marked";

import { useAuth } from "../../contexts/AuthContext";

import styles from "../AddRecordForm/AddRecordForm.module.scss";

const RECORD_TYPES = [
    "Text",
    "Json",
    "Xml",
    "Set",
    "Vector"
] as const;

type RecordType = typeof RECORD_TYPES[number];

interface FormData {
    title: string;
    parentId: string;
    visible: boolean;
    flags: number;
    postAt: string;
    refreshAt: string;

    value: string;
    markdown: string;
    xml: string;
    json: string;
    texts: string;
    vector: string;
    blob: File | null;

    tags: string;
}

interface Payload {
    id?: string;

    title: string;
    visible: boolean;
    flags: number;
    postAt: string;
    refreshAt: string;

    parentId?: string;
    tags: string[];

    [key: string]: unknown;
}

interface RecordConfig {
    endpoint: string;
    deleteEndpoint: string;
    buildPayload: (form: FormData) => object;
}

interface RecordData {
    id: string;
    type: string;
    title: string;
    parentId?: string;
    visible: boolean;
    flags: number;
    postAt: string;
    refreshAt?: string;
    markdown?: string;
    json?: unknown;
    xml?: string;
    texts?: string[];
    vector?: number[];
    value?: string;
    tags?: string[];
}

const EMPTY_FORM: FormData = {
    title: "",
    parentId: "",
    visible: true,
    flags: 0,
    postAt: new Date().toISOString(),
    refreshAt: new Date().toISOString(),

    value: "",
    markdown: "",
    xml: "",
    json: "{}",
    texts: "",
    vector: "",
    blob: null,

    tags: ""
};

function parseTags(tags: string): string[] {
    return tags
        .split(",")
        .map(tag => tag.trim())
        .filter(Boolean);
}

export default function EditRecordForm() {

    const { id } = useParams();
    const navigate = useNavigate();
    const { token } = useAuth();
    const [loading, setLoading] = useState(true);
    const [recordId, setRecordId] = useState("");
    const [type, setType] = useState<RecordType>("Text");
    const [form, setForm] = useState<FormData>(EMPTY_FORM);

    const RECORD_CONFIG: Record<RecordType, RecordConfig> = {
        Text: {
            endpoint: "/api/v2/record/markdown",
            deleteEndpoint: "/api/v2/record/markdown",
            buildPayload: form => ({
                markdown: form.markdown
            })
        },
        Json: {
            endpoint: "/api/v2/record/json",
            deleteEndpoint: "/api/v2/record/json",
            buildPayload: form => ({
                json: JSON.parse(form.json)
            })
        },
        Xml: {
            endpoint: "/api/v2/record/xml",
            deleteEndpoint: "/api/v2/record/xml",
            buildPayload: form => ({
                xml: form.xml
            })
        },
        Set: {
            endpoint: "/api/v2/record/set",
            deleteEndpoint: "/api/v2/record/set",
            buildPayload: form => ({
                texts: form.texts
                    .split("\n")
                    .map(x => x.trim())
                    .filter(Boolean)
            })
        },
        Vector: {
            endpoint: "/api/v2/record/vector",
            deleteEndpoint: "/api/v2/record/vector",
            buildPayload: form => ({
                vector: form.vector
                    .split(",")
                    .map(x => Number(x.trim()))
            })
        }
    };

    const update = (
        field: keyof FormData,
        value: FormData[keyof FormData]
    ) => {

        setForm(prev => ({
            ...prev,
            [field]: value
        }));

    };

    function recordTypeFromServer(type: string): RecordType {
        switch (type.toLowerCase()) {
            case "markdown":
            case "text":
                return "Text";
            case "json":
                return "Json";
            case "xml":
                return "Xml";
            case "set":
                return "Set";
            case "vector":
                return "Vector";
            default:
                return "Text";
        }
    }

    function recordToForm(record: RecordData): FormData {
        return {
            title: record.title,
            parentId: record.parentId ?? "",
            visible: record.visible,
            flags: record.flags,
            postAt: record.postAt,
            refreshAt: record.refreshAt ?? new Date().toISOString(),
            markdown: record.markdown ?? "",
            json: record.json
                ? JSON.stringify(record.json, null, 2)
                : "{}",
            xml: record.xml ?? "",
            texts: record.texts?.join("\n") ?? "",
            vector: record.vector?.join(", ") ?? "",
            value: record.value ?? "",
            blob: null,
            tags: record.tags?.join(", ") ?? ""
        };
    }

    useEffect(() => {
        async function loadRecord() {
            try {
                const response = await axios.get(
                    `/api/v2/records/${id}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }
                );
                const record: RecordData = response.data;
                console.log(response);
                console.log(response.data);
                setRecordId(record.id);
                setType(recordTypeFromServer(record.type));
                setForm(recordToForm(record));
            } catch (e) {
                console.error(e);
                alert("Не удалось загрузить запись.");
                navigate("/");
            } finally {
                setLoading(false);
            }
        }

        if (id && token) {
            loadRecord();
        }
    }, [id, token, navigate]);

    if (loading) {
        return (
            <div className={styles.container}>
                <h2>Загрузка...</h2>
            </div>
        );
    }

    const submit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            const config = RECORD_CONFIG[type];
            const refreshAt =
                form.refreshAt && form.refreshAt.length > 0
                    ? form.refreshAt
                    : new Date().toISOString();
            let payload: Payload = {
                id: recordId,
                title: form.title,
                visible: form.visible,
                flags: Number(form.flags),
                postAt: form.postAt,
                refreshAt,
                tags: form.tags
                        .split(",")
                        .map(tag => tag.trim())
                        .filter(Boolean),
                ...config.buildPayload(form)
            };
            if (form.parentId.trim()) {
                payload.parentId = form.parentId.trim();
            }
            /* payload.tags = parseTags(form.tags); */
            await axios.put(
                config.endpoint,
                payload,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );
            alert("Запись успешно сохранена.");
            navigate("/");
        } catch (error) {
            console.error(error);
            if (axios.isAxiosError(error)) {
                if (error.response?.status === 400) {
                    alert("Некорректные данные.");
                    return;
                }
                if (error.response?.status === 401) {
                    alert("Необходимо выполнить вход.");
                    return;
                }
                if (error.response?.status === 403) {
                    alert("Недостаточно прав.");
                    return;
                }
                if (error.response?.status === 404) {
                    alert("Запись не найдена.");
                    return;
                }
            }
            alert("Ошибка сохранения записи.");
        }
    };

    const remove = async () => {
        if (!window.confirm("Удалить запись?")) {
            return;
        }
        try {
            const config = RECORD_CONFIG[type];
            await axios.delete(
                `${config.deleteEndpoint}/${recordId}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );
            alert("Запись успешно удалена.");
            navigate("/");
        } catch (error) {
            console.error(error);
            if (axios.isAxiosError(error)) {
                switch (error.response?.status) {
                    case 400:
                        alert("Некорректный запрос.");
                        return;
                    case 401:
                        alert("Необходимо выполнить вход.");
                        return;
                    case 403:
                        alert("Недостаточно прав.");
                        return;
                    case 404:
                        alert("Запись не найдена.");
                        return;
                }
            }
            alert("Ошибка удаления записи.");
        }
    };

    return (
        <div className={styles.container}>
            <div className={styles.formWrapper}>
                <form className={styles.form} onSubmit={submit}>
                    <h1 className={styles.formTitle}>Редактировать запись</h1>
                    <table className={styles.blueTable}>
                    <tbody>
                    <tr>
                    <td>
                    <div className={styles.formGroup}>
                        <select
                            className={styles.input}
                            value={type}
                            disabled
                        >
                            {RECORD_TYPES.map(t => (
                                <option key={t}>{t}</option>
                            ))}
                        </select>
                    </div>
                    </td>
                    <td>
                    <div className={styles.formGroup}>
                        <input
                            name="Title"
                            id="Title"
                            placeholder="Title"
                            className={styles.input}
                            value={form.title}
                            onChange={e => update("title", e.target.value)}
                        />
                    </div>
                    </td>
                    </tr>
                    <tr>
                    <td>
                    <div className={styles.formGroup}>
                        <input
                            className={styles.input}
                            value={form.parentId}
                            name="parentId"
                            id="parentId"
                            placeholder="Parent id"
                            onChange={e => update("parentId", e.target.value)}
                        />
                    </div>
                    </td>
                    <td>
                    <div className={styles.formGroup}>
                        <input
                            className={styles.input}
                            name="refreshAt"
                            id="refreshAt"
                            type="datetime-local"
                            value={form.refreshAt ? form.refreshAt.substring(0, 16) : ""}
                            onChange={e =>
                                update(
                                    "refreshAt",
                                    new Date(e.target.value).toISOString()
                                )
                            }
                        />
                    </div>
                    </td>
                    </tr>
                    <tr>
                    <td className={styles.rightAlign}>
                    <div className={styles.formGroup}>
                        <input
                            name="visible"
                            id="visible"
                            placeholder="visible"
                            type="checkbox"
                            checked={form.visible}
                            onChange={e => update("visible", e.target.checked)}
                        />
                    </div>
                    </td>
                    <td><label>visible</label></td>
                    </tr>
                    <tr>
                    <td colSpan={2}>
                    <div className={styles.formGroup}>
                        <input
                            className={styles.input}
                            name="tags"
                            id="tags"
                            placeholder="Tags (через запятую)"
                            value={form.tags}
                            onChange={e => update("tags", e.target.value)}
                        />
                    </div>
                    </td>
                    </tr>
                    <tr>
                    <td>
                        &nbsp;
                    {type === "Text" && (
                        <div className={styles.formGroup}>
                            <textarea
                                className={styles.input}
                                name="markdown"
                                id="markdown"
                                placeholder="Markdown"
                                rows={10}
                                value={form.markdown}
                                onChange={e => update("markdown", e.target.value)}
                            />
                        </div>
                    )}

                    {type === "Json" && (
                        <div className={styles.formGroup}>
                            <textarea
                                className={styles.input}
                                name="json"
                                id="json"
                                placeholder="JSON"
                                rows={10}
                                value={form.json}
                                onChange={e => update("json", e.target.value)}
                            />
                        </div>
                    )}

                    {type === "Xml" && (
                        <div className={styles.formGroup}>
                            <textarea
                                className={styles.input}
                                name="xml"
                                id="xml"
                                placeholder="XML"
                                rows={10}
                                value={form.xml}
                                onChange={e => update("xml", e.target.value)}
                            />
                        </div>
                    )}

                    {type === "Set" && (
                        <div className={styles.formGroup}>
                            <textarea
                                className={styles.input}
                                name="texts"
                                id="texts"
                                placeholder="Строки (каждая с новой строки)"
                                rows={10}
                                value={form.texts}
                                onChange={e => update("texts", e.target.value)}
                            />
                        </div>
                    )}

                    {type === "Vector" && (
                        <div className={styles.formGroup}>
                            <textarea
                                className={styles.input}
                                name="vector"
                                id="vector"
                                placeholder="Вектор (1,2,3,4)"
                                rows={10}
                                value={form.vector}
                                onChange={e => update("vector", e.target.value)}
                            />
                        </div>
                    )}
                    </td>
                    <td className={styles.topAlign}>&nbsp;
                    {type === "Text" && (
                    <div
                        className={styles.outputStyle}
                        dangerouslySetInnerHTML={{
                            __html: marked.parse(form.markdown || "") as string
                        }}>
                    </div>
                    )}
                    {type !== "Text" && (<div className={styles.outputStyle}></div>)}
                    </td>
                    </tr>
                    </tbody>
                    </table>
                    <div className={styles.buttonGroup}>
                        <button className={styles.submitButton} type="submit">
                            Сохранить
                        </button>

                        <button
                            type="button"
                            className={styles.submitButton}
                            onClick={remove}
                        >
                            Удалить
                        </button>

                        <button
                            type="button"
                            className={styles.submitButton}
                            onClick={() => navigate("/")}
                        >
                            Отмена
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}