import React, { useEffect, useRef, useState, useCallback } from "react";
import { getRecords } from "../service/records-api";
import styles from './IntersectionObserverRecordsPage.module.scss';
import Record from "./Record/Record";

interface RecordDto {
    id: string;
    title: string;
    markdown?: string;
}

export default function RecordsPage() {
    const [records, setRecords] = useState<RecordDto[]>([]);
    const [hasMore, setHasMore] = useState(true);

    const loaderRef = useRef<HTMLDivElement | null>(null);

    const pageRef = useRef(0);
    const loadingRef = useRef(false);
    const initialLoadRef = useRef(false);

    const loadPage = useCallback(async () => {
        if (loadingRef.current || !hasMore) {
            return;
        }

        loadingRef.current = true;

        try {
            const currentPage = pageRef.current;

            const data = await getRecords(currentPage, 10);

            setRecords(prev => [...prev, ...(data.list ?? [])]);

            pageRef.current++;

            const total = data.totalRecords ?? 0;
            const more = (currentPage + 1) * 10 < total;
            setHasMore(more);

            // если после рендера нет вертикального скролла —
            // сразу грузим ещё
            requestAnimationFrame(() => {
                if (
                    more &&
                    document.documentElement.scrollHeight <= window.innerHeight
                ) {
                    loadPage();
                }
            });

        } finally {
            loadingRef.current = false;
        }

    }, [hasMore]);


    // первая загрузка
    useEffect(() => {
        if (initialLoadRef.current) {
            return;
        }
        initialLoadRef.current = true;
        loadPage();
    }, [loadPage]);


    // observer
    useEffect(() => {
        const observer = new IntersectionObserver(entries => {
            if (entries[0].isIntersecting) {
                console.log("LOAD NEXT");
                loadPage();
            }
        });
        const loader = loaderRef.current;
        if (loader) {
            observer.observe(loader);
        }
        return () => {
            observer.disconnect();
        };
    }, [loadPage]);


    return (
        <div className={styles.container}>
            <div key="root" className={styles.userContainerRoot}>
            </div>
            {records.map(record => (
                <div key={record.id} className={styles.userContainer}>
                    <Record record={record}/>
                </div>
            ))}
            <div ref={loaderRef} style={{ height: 40 }} />
        </div>
    );
}