// services/records-api.js

import axios from "axios";

export const getRecords = async (page = 0, size = 10) => {
    const response = await axios.get("/api/v2/records", {
        params: { page, size }
    });

    return response.data;
};