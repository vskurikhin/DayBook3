/*
 * This file was last modified at 2026.07.03 12:04 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * UpdateSetRecord.java
 * $Id$
 */

package su.svn.core.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO used for updating an existing set record.
 *
 * @param id        record identifier
 * @param parentId  parent record identifier
 * @param title     record title
 * @param texts     updated set values
 * @param postAt    original creation timestamp
 * @param refreshAt update timestamp
 * @param visible   visibility flag
 * @param flags     custom bit flags
 * @param tags      associated tags
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateSetRecord(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty UUID id,
        @Schema(defaultValue = "00000000-0000-0000-0000-000000000000", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty UUID parentId,
        @JsonProperty String title,
        @JsonProperty String aHref,
        @JsonProperty Set<String> texts,
        @JsonProperty OffsetDateTime postAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty OffsetDateTime refreshAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder
    public UpdateSetRecord {
        if (tags == null) tags = new HashSet<>();
    }
}
