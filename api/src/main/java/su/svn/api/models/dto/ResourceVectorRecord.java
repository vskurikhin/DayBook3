/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ResourceVectorRecord.java
 * $Id$
 */

package su.svn.api.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO representing a vector record returned from API resources.
 *
 * <p>This object is used as an outbound representation of vector records.
 * It contains identifiers, vector data, timestamps, and metadata.</p>
 *
 * <h2>JSON Serialization</h2>
 * <ul>
 *     <li>Null fields are excluded from JSON output</li>
 *     <li>{@code userName} is ignored during serialization</li>
 * </ul>
 *
 * @param id resource UUID
 * @param parentId parent resource UUID
 * @param title optional title
 * @param vector vector embedding values
 * @param userName internal user name
 * @param postAt publication timestamp
 * @param refreshAt refresh timestamp
 * @param visible visibility flag
 * @param flags custom flags
 */
@JsonPropertyOrder({"id", "visible", "flags"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResourceVectorRecord(
        @JsonProperty UUID id,
        @JsonProperty UUID parentId,
        @JsonProperty String title,
        @JsonProperty String aHref,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty float[] vector,
        @JsonIgnore String userName,
        @JsonProperty OffsetDateTime postAt,
        @JsonProperty OffsetDateTime refreshAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder(toBuilder = true)
    public ResourceVectorRecord {
    }
}
