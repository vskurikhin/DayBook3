/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * NewValueRecord.java
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
 * DTO for creating a new value record.
 *
 * <p>
 * Contains the data required to create a new textual value-based record,
 * including metadata such as title, visibility flags, posting time,
 * and associated tags.
 * </p>
 *
 * @param parentId identifier of the parent record
 * @param title optional title of the record
 * @param value textual value content
 * @param postAt publication timestamp
 * @param visible visibility flag
 * @param flags additional bit flags
 * @param tags associated tags
 */
@JsonPropertyOrder({"visible", "flags"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NewValueRecord(
        @JsonProperty UUID parentId,
        @Size(max = 4096, message = "Title must be at most 4096 characters")
        @JsonProperty String title,
        @Size(max = 4096, message = "a-href must be at most 4096 characters")
        @JsonProperty String aHref,
        @NotNull(message = "Value is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(max = 4096, message = "Value must be at most 255 characters")
        @JsonProperty String value,
        @NotNull(message = "Post at is required")
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty OffsetDateTime postAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder
    public NewValueRecord {
    }
}
