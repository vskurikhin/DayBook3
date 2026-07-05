/*
 * This file was last modified at 2026.05.22 19:39 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordData.java
 * $Id$
 */

package su.svn.api.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Universal DTO representing record content and metadata.
 *
 * <p>
 * This data transfer object is used for transporting different
 * types of records through API layers and services.
 * </p>
 *
 * <p>
 * Depending on the {@link su.svn.lib.RecordType},
 * only a subset of content fields may be populated.
 * </p>
 *
 * <h2>Supported Content Types</h2>
 * <ul>
 *     <li>{@code blob} - binary data</li>
 *     <li>{@code json} - structured JSON key-value data</li>
 *     <li>{@code texts} - collection of text values</li>
 *     <li>{@code fileName} - file name content</li>
 *     <li>{@code html} - HTML formatted content</li>
 *     <li>{@code link} - URL or hyperlink value</li>
 *     <li>{@code markdown} - Markdown formatted content</li>
 *     <li>{@code value} - plain text value</li>
 * </ul>
 *
 * <p>
 * Includes common metadata such as timestamps,
 * visibility flags, hierarchical relationships,
 * and record identifiers.
 * </p>
 *
 * @param id unique identifier of the record
 * @param parentId identifier of the parent record
 * @param type type of the record
 * @param postAt publication timestamp
 * @param refreshAt refresh timestamp
 * @param visible visibility flag
 * @param flags additional bit flags
 * @param title optional title of the record
 * @param blob binary content
 * @param json structured JSON content
 * @param texts collection of textual values
 * @param fileName file name content
 * @param html HTML content
 * @param link hyperlink or URL value
 * @param markdown Markdown formatted content
 * @param value plain textual value
 */
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public record RecordData(
        @JsonProperty UUID id,
        @JsonProperty UUID parentId,
        @JsonProperty su.svn.lib.RecordType type,
        @JsonProperty OffsetDateTime postAt,
        @JsonProperty OffsetDateTime refreshAt,
        @JsonProperty Boolean visible,
        @JsonProperty int flags,
        @JsonProperty String title,
        @JsonProperty String aHref,
        @JsonProperty byte[] blob,
        @JsonProperty Map<String, String> json,
        @JsonProperty Set<String> texts,
        @JsonProperty String fileName,
        @JsonProperty String html,
        @JsonProperty String link,
        @JsonProperty String markdown,
        @JsonProperty String value,
        @JsonProperty float[] vector,
        @JsonProperty String xml,
        @JsonProperty List<String> tags
) implements Serializable {
    @Builder
    public RecordData {
    }
}
