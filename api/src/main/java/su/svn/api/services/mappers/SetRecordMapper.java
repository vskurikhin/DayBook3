/*
 * This file was last modified at 2026.05.22 19:39 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * SetRecordMapper.java
 * $Id$
 */

package su.svn.api.services.mappers;

import org.mapstruct.*;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.ResourceSetRecord;
import su.svn.api.models.dto.UpdateSetRecord;

@Mapper(componentModel = "cdi")
public interface SetRecordMapper extends DateTimeMapper {

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "type", constant = "Set")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "aHref", source = "aHref")
    @Mapping(target = "texts", source = "texts")
    @Mapping(target = "postAt", source = "postAt")
    @Mapping(target = "refreshAt", source = "refreshAt")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "flags", source = "flags")
    @Mapping(target = "tags", source = "tags")
    PostRecord toEntity(ResourceSetRecord record);

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "type", constant = "Set")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "aHref", source = "aHref")
    @Mapping(target = "texts", source = "texts")
    @Mapping(target = "postAt", source = "postAt")
    @Mapping(target = "refreshAt", source = "refreshAt")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "flags", source = "flags")
    @Mapping(target = "tags", source = "tags")
    PostRecord toEntity(UpdateSetRecord record);

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    ResourceSetRecord toResource(PostRecord postRecord);
}
