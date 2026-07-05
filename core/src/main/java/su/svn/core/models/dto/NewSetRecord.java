/*
 * This file was last modified at 2026.07.03 12:04 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * NewSetRecord.java
 * $Id$
 */

package su.svn.core.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO used for creating a new set record.
 *
 * @param parentId parent record identifier
 * @param title    record title
 * @param texts    set of unique string values
 * @param postAt   creation/post timestamp
 * @param visible  visibility flag
 * @param flags    custom bit flags
 * @param tags     associated tags
 */
@JsonPropertyOrder({"visible", "flags"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NewSetRecord(
        @Schema(defaultValue = "00000000-0000-0000-0000-000000000000", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty UUID parentId,
        @JsonProperty String title,
        @JsonProperty String aHref,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty Set<String> texts,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty OffsetDateTime postAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder
    public NewSetRecord {
        if (tags == null) tags = new HashSet<>();
    }
}
