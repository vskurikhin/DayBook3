export interface RecordDto {
    id: string;
    title: string;
    markdown?: string;
}

export interface RecordsResponse {
    list: RecordDto[];
    totalRecords: number;
}

export function getRecords(
    page: number,
    size: number
): Promise<RecordsResponse>;