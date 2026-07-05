/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ResourceXmlRecord.java
 * $Id$
 */

package su.svn.core.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO representing an XML record returned to clients.
 *
 * <p>
 * Contains XML content, metadata, and timestamps used for API responses.
 * </p>
 *
 * @param id        unique record identifier
 * @param parentId  parent record identifier
 * @param title     record title
 * @param xml       XML document content
 * @param userName  owner username
 * @param postAt    publication timestamp
 * @param refreshAt refresh timestamp
 * @param visible   visibility flag
 * @param flags     custom bit flags
 * @param tags      associated tags
 */
@JsonPropertyOrder({"id", "visible", "flags"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResourceXmlRecord(
        @JsonProperty UUID id,
        @JsonProperty UUID parentId,
        @JsonProperty String title,
        @JsonProperty String aHref,
        @JsonProperty String xml,
        @JsonIgnore String userName,
        @JsonProperty OffsetDateTime postAt,
        @JsonProperty OffsetDateTime refreshAt,
        @JsonProperty boolean visible,
        @JsonProperty int flags,
        @JsonProperty Set<String> tags) implements Serializable {
    @Builder(toBuilder = true)
    public ResourceXmlRecord {
    }
}
