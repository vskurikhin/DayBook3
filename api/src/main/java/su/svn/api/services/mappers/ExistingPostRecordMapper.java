/*
 * This file was last modified at 2026.07.01 22:56 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ExistingPostRecordMapper.java
 * $Id$
 */

package su.svn.api.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import su.svn.api.domain.entities.PostRecord;

@Mapper(componentModel = "cdi")
public interface ExistingPostRecordMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "sequenceId", ignore = true)
    void updateExistingRecord(@MappingTarget PostRecord to, PostRecord from);
}
