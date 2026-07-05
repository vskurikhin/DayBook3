/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * NewVectorRecord.java
 * $Id$
 */

package su.svn.api.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO representing a request for creating a new vector-based record.
 *
 * <p>This record is used as an input payload for vector resource creation APIs.
 * It contains metadata, vector embedding values, visibility flags,
 * timestamps, and optional tags.</p>
 *
 * <h2>JSON Serialization</h2>
 * <ul>
 *     <li>Null fields are excluded from JSON output</li>
 *     <li>Properties {@code visible} and {@code flags} are serialized last</li>
 * </ul>
 *
 * <h2>Fields</h2>
 * <ul>
 *     <li>{@code parentId} – parent record identifier</li>
 *     <li>{@code title} – optional title</li>
 *     <li>{@code vector} – required vector embedding values</li>
 *     <li>{@code postAt} – creation/publication timestamp</li>
 *     <li>{@code visible} – visibility flag</li>
 *     <li>{@code flags} – custom bit flags</li>
 *     <li>{@code tags} – optional tag collection</li>
 * </ul>
 *
 * @param parentId parent record UUID
 * @param title optional title
 * @param vector vector embedding data
 * @param postAt publication timestamp
 * @param visible visibility flag
 * @param flags custom flags
 * @param tags optional tags
 */
@JsonPropertyOrder({"visible", "flags"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NewVectorRecord(
        @JsonProperty UUID parentId,
        @Size(max = 4096, message = "Title must be at most 4096 characters")
        @JsonProperty String title,
        @Size(max = 4096, message = "a-href must be at most 4096 characters")
        @JsonProperty String aHref,
        @NotNull(message = "Vector at is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 1024, max = 1024, message = "Vector must be at most 1024 elements")
        @JsonProperty float[] vector,
        @NotNull(message = "Post at is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty OffsetDateTime postAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder
    public NewVectorRecord {
    }
}
