/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * NewMarkdownRecord.java
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
 * DTO used for creating a new markdown record.
 *
 * <p>
 * Contains markdown content and metadata required
 * for creating a record in the remote core service.
 * </p>
 *
 * @param parentId parent record identifier
 * @param title record title
 * @param markdown markdown content
 * @param postAt publication timestamp
 * @param visible visibility flag
 * @param flags additional bitmask flags
 * @param tags associated tags
 */
@JsonPropertyOrder({"visible", "flags"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NewMarkdownRecord(
        @JsonProperty UUID parentId,
        @Size(max = 4096, message = "Title must be at most 4096 characters")
        @JsonProperty String title,
        @Size(max = 4096, message = "a-href must be at most 4096 characters")
        @JsonProperty String aHref,
        @NotNull(message = "Markdown is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(max = 65535, message = "Markdown must be at most 65535 characters")
        @JsonProperty String markdown,
        @NotNull(message = "Post at is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty OffsetDateTime postAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder
    public NewMarkdownRecord {
    }
}
