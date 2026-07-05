/*
 * This file was last modified at 2026.05.21 23:42 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * TagMapper.java
 * $Id$
 */

package su.svn.core.services.mappers;

import org.mapstruct.Mapper;
import su.svn.core.domain.entities.Tag;

/**
 * Mapper for converting between {@link Tag} entities
 * and string tag representations.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface TagMapper {

    default Tag map(String value) {
        return Tag.builder()
                .tag(value)
                .build();
    }

    default String map(Tag value) {
        return value.tag();
    }
}
