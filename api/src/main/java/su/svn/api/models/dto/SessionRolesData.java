/*
 * This file was last modified at 2026.05.21 16:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * SessionRolesData.java
 * $Id$
 */

package su.svn.api.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SessionRolesData(
        @JsonProperty Boolean success,
        @JsonProperty UserHasRoles data) implements Serializable {
    @Builder
    public SessionRolesData {
    }
}
