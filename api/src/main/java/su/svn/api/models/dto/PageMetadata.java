/*
 * This file was last modified at 2026.05.21 16:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * PageMetadata.java
 * $Id$
 */

package su.svn.api.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

/**
 * PageMetadata
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageMetadata(
        @JsonProperty Long size,
        @JsonProperty Long totalElements,
        @JsonProperty Long totalPages,
        @JsonProperty Long number) implements Serializable {
    @Builder
    public PageMetadata {
    }

    public PageMetadata() {
        this(Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE);
    }
}
