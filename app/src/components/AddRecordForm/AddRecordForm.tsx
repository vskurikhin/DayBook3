import Badge from "react-bootstrap/Badge";
import React, { useState } from "react";
import axios from "axios";
import { Checkbox } from 'primereact/checkbox';
import { marked } from "marked";
import { useNavigate } from "react-router-dom";

import { useAuth } from "../../contexts/AuthContext";

import styles from "./AddRecordForm.module.scss";

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

    value: string;
    markdown: string;
    xml: string;
    json: string;
    texts: string;
    vector: string;
    blob: File | null;

    tags: string;
}

interface RecordConfig {
    endpoint: string;
    buildPayload: (form: FormData) => object;
}

interface Payload {
    title: string;
    visible: boolean;
    flags: number;
    postAt: string;
    parentId?: string;
    tags?: string[];
    [key: string]: unknown;
}


export default function AddRecordForm() {
    const navigate = useNavigate();
    const { token } = useAuth();
    const [type, setType] = useState<RecordType>("Text");
    const [form, setForm] = useState<FormData>({
        title: "",
        parentId: "",
        visible: true,
        flags: 0,
        postAt: new Date().toISOString(),

        value: "",
        markdown: "",
        xml: "",
        json: "{}",
        texts: "",
        vector: "",
        blob: null,

        tags: ""
    });
    const update = (
        field: keyof FormData,
        value: FormData[keyof FormData]
    ) => {
        setForm(prev => ({
            ...prev,
            [field]: value
        }));
    };
    const RECORD_CONFIG: Record<RecordType, RecordConfig> = {
        Text: {
            endpoint: "/api/v2/record/markdown",
            buildPayload: (form) => ({
                markdown: form.markdown
            })
        },

        Json: {
            endpoint: "/api/v2/record/json",
            buildPayload: (form) => ({
                json: JSON.parse(form.json)
            })
        },

        Xml: {
            endpoint: "/api/v2/record/xml",
            buildPayload: (form) => ({
                xml: form.xml
            })
        },

        Set: {
            endpoint: "/api/v2/record/set",
            buildPayload: (form) => ({
                texts: form.texts
                    .split("\n")
                    .map(x => x.trim())
                    .filter(Boolean)
            })
        },

        Vector: {
            endpoint: "/api/v2/record/vector",
            buildPayload: (form) => ({
                vector: form.vector
                    .split(",")
                    .map(x => Number(x.trim()))
            })
        }
    };
    const submit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const config = RECORD_CONFIG[type];

        let payload: Payload = {
            title: form.title,
            visible: form.visible,
            flags: Number(form.flags),
            postAt: form.postAt,

            ...config.buildPayload(form)
        };

        if (form.parentId) {
            payload.parentId = form.parentId;
        }

        if (form.tags.trim()) {
            payload.tags = form.tags
                .split(",")
                .map(x => x.trim())
                .filter(Boolean);
        }

        const response = await axios.post(
            config.endpoint,
            payload,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        console.log(response.data);
    };

    return (
        <div className={styles.container}>
            <div className={styles.formWrapper}>
                <form className={styles.form} onSubmit={submit}>
                    <h1 className={styles.formTitle}>Добавить запись</h1>
                    <table className={styles.blueTable}>
                    <tbody>
                    <tr>
                    <td>
                    <div className={styles.formGroup}>
                        <select
                            className={styles.input}
                            value={type}
                            onChange={(e: React.ChangeEvent<HTMLSelectElement>) => {
                                const value = e.target.value as RecordType;
                                setType(value);
                            }}
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
                            name="postAt"
                            id="postAt"
                            placeholder="Post at"
                            type="datetime-local"
                            onChange={e =>
                                update(
                                    "postAt",
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