/*
 * This file was last modified at 2026.06.29 16:59 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ResourceRecordView.java
 * $Id$
 */

package su.svn.lib.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@JsonPropertyOrder({"id", "visible", "flags"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ResourceRecordView(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty UUID id,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty UUID parentId,
        @JsonProperty su.svn.lib.RecordType type,
        @JsonProperty String userName,
        @JsonProperty OffsetDateTime postAt,
        @JsonProperty OffsetDateTime refreshAt,
        @JsonProperty LocalDateTime lastChangedTime,
        @JsonProperty boolean enabled,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty String title,
        @JsonProperty String aHref,
        @JsonProperty byte[] blob,
        @JsonProperty Map<String, String> json,
        @JsonProperty Set<String> texts,
        @JsonProperty String fileName,
        @JsonProperty String html,
        @JsonProperty String link,
        @JsonProperty String markdown,
        @JsonProperty String value,
        @JsonProperty float[] vector,
        @JsonProperty String xml,
        @JsonProperty List<String> tags
) implements Serializable {
    @Builder(toBuilder = true)
    public ResourceRecordView {
    }
}












