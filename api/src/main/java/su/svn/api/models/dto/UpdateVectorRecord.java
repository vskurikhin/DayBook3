/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * UpdateVectorRecord.java
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
 * DTO representing an update request for an existing vector record.
 *
 * <p>This payload is used for updating mutable vector resource properties
 * including vector values, timestamps, flags, and metadata.</p>
 *
 * <h2>Validation Rules</h2>
 * <ul>
 *     <li>{@code id} is required</li>
 *     <li>{@code vector} is required</li>
 *     <li>{@code refreshAt} is required</li>
 * </ul>
 *
 * @param id resource UUID
 * @param parentId parent resource UUID
 * @param title optional title
 * @param vector vector embedding values
 * @param postAt publication timestamp
 * @param refreshAt refresh timestamp
 * @param visible visibility flag
 * @param flags custom flags
 * @param tags optional tags
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateVectorRecord(
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
        @NotNull(message = "Vector is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 1024, max = 1024, message = "Vector must be at most 1024 elements")
        @JsonProperty float[] vector,
        @JsonProperty OffsetDateTime postAt,
        @NotNull(message = "Refresh at is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty OffsetDateTime refreshAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder
    public UpdateVectorRecord {
    }
}
