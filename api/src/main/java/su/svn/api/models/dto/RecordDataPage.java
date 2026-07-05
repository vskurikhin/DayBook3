/*
 * This file was last modified at 2026.05.31 00:28 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordDataPage.java
 * $Id$
 */

package su.svn.api.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@JsonInclude
@RegisterForReflection
public class RecordDataPage extends Page<RecordData> {
    public RecordDataPage(List<RecordData> list, long pageCount, long pageIndex, long pageSize, long totalRecords) {
        super(list, pageCount, pageIndex, pageSize, totalRecords);
    }
}
