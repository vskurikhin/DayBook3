/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * UpdateXmlRecord.java
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
 * DTO representing an update request for an existing XML record.
 *
 * <p>
 * This payload is used for updating XML resource properties,
 * including XML content, metadata, timestamps, visibility flags,
 * and associated tags.
 * </p>
 *
 * <h2>Validation Rules</h2>
 * <ul>
 *     <li>{@code id} is required</li>
 *     <li>{@code xml} is required</li>
 *     <li>{@code refreshAt} is required</li>
 * </ul>
 *
 * @param id unique resource identifier
 * @param parentId parent resource identifier
 * @param title optional title
 * @param xml XML document content
 * @param postAt publication timestamp
 * @param refreshAt refresh timestamp
 * @param visible visibility flag
 * @param flags custom flags
 * @param tags optional tag collection
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateXmlRecord(
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
        @NotNull(message = "XML is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(max = 10485760, message = "XML must be at most 10485760 characters")
        @JsonProperty String xml,
        @JsonProperty OffsetDateTime postAt,
        @NotNull(message = "Refresh at is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty OffsetDateTime refreshAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder
    public UpdateXmlRecord {
    }
}
