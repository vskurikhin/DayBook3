/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * NewXmlRecord.java
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
 * DTO representing a request for creating a new XML record.
 *
 * <p>
 * This object is used as an input payload for XML resource creation APIs.
 * It contains XML content, metadata, timestamps, visibility settings,
 * and optional tags.
 * </p>
 *
 * <h2>JSON Serialization</h2>
 * <ul>
 *     <li>Null fields are excluded from JSON output</li>
 *     <li>{@code visible} and {@code flags} are serialized last</li>
 * </ul>
 *
 * @param parentId parent record identifier
 * @param title    optional record title
 * @param xml      XML document content
 * @param postAt   publication timestamp
 * @param visible  visibility flag
 * @param flags    custom flags
 * @param tags     optional tag collection
 */
@JsonPropertyOrder({"visible", "flags"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NewXmlRecord(
        @JsonProperty UUID parentId,
        @Size(max = 4096, message = "Title must be at most 4096 characters")
        @JsonProperty String title,
        @Size(max = 4096, message = "a-href must be at most 4096 characters")
        @JsonProperty String aHref,
        @NotNull(message = "XML is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(max = 10485760, message = "XML must be at most 10485760 characters")
        @JsonProperty String xml,
        @NotNull(message = "Post at is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty OffsetDateTime postAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder
    public NewXmlRecord {
    }
}
