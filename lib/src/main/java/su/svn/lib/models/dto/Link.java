/*
 * This file was last modified at 2026.06.29 16:59 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * Link.java
 * $Id$
 */

package su.svn.lib.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

/**
 * Link
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Link(
        @JsonProperty String href,
        @JsonProperty String hreflang,
        @JsonProperty String title,
        @JsonProperty String type,
        @JsonProperty String deprecation,
        @JsonProperty String profile,
        @JsonProperty String name,
        @JsonProperty Boolean templated) implements Serializable {
    @Builder
    public Link {
    }
}
