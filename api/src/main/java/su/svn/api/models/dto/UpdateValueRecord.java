/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * UpdateValueRecord.java
 * $Id$
 */

package su.svn.api.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for updating an existing value record.
 *
 * <p>
 * Contains all mutable fields required for updating
 * an existing value record.
 * </p>
 *
 * @param id unique identifier of the record
 * @param parentId identifier of the parent record
 * @param title optional title of the record
 * @param value textual value content
 * @param postAt publication timestamp
 * @param refreshAt refresh timestamp
 * @param visible visibility flag
 * @param flags additional bit flags
 * @param tags associated tags
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateValueRecord(
        @NotNull(message = "Id is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty UUID id,
        @NotNull(message = "parentId at is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty UUID parentId,
        @Size(max = 4096, message = "Title must be at most 4096 characters")
        @JsonProperty String title,
        @Size(max = 4096, message = "a-href must be at most 4096 characters")
        @JsonProperty String aHref,
        @NotNull(message = "Value is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(max = 4096, message = "Value must be at most 255 characters")
        @JsonProperty String value,
        @JsonProperty OffsetDateTime postAt,
        @NotNull(message = "Refresh at is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty OffsetDateTime refreshAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder
    public UpdateValueRecord {
    }
}
