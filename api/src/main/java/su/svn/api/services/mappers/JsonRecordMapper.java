/*
 * This file was last modified at 2026.06.29 16:59 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecordMapper.java
 * $Id$
 */

package su.svn.api.services.mappers;

import org.mapstruct.*;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.ResourceJsonRecord;
import su.svn.api.models.dto.UpdateJsonRecord;

@Mapper(componentModel = "cdi")
public interface JsonRecordMapper extends DateTimeMapper {

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "type", constant = "Blob")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "aHref", source = "aHref")
    @Mapping(target = "json", source = "json")
    @Mapping(target = "postAt", source = "postAt")
    @Mapping(target = "refreshAt", source = "refreshAt")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "flags", source = "flags")
    @Mapping(target = "tags", source = "tags")
    PostRecord toEntity(ResourceJsonRecord record);

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "type", constant = "Blob")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "aHref", source = "aHref")
    @Mapping(target = "json", source = "json")
    @Mapping(target = "postAt", source = "postAt")
    @Mapping(target = "refreshAt", source = "refreshAt")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "flags", source = "flags")
    @Mapping(target = "tags", source = "tags")
    PostRecord toEntity(UpdateJsonRecord record);

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    ResourceJsonRecord toResource(PostRecord postRecord);
}
