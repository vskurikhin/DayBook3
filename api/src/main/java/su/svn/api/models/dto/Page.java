/*
 * This file was last modified at 2026.05.31 00:28 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * Page.java
 * $Id$
 */

package su.svn.api.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Accessors(fluent = true)
@Builder
@Data
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
@RequiredArgsConstructor
@ToString
public class Page<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    @JsonProperty
    List<T> list;
    @JsonProperty
    long pageCount;
    @JsonProperty
    long pageIndex;
    @JsonProperty
    long pageSize;
    @JsonProperty
    long totalRecords;
}
