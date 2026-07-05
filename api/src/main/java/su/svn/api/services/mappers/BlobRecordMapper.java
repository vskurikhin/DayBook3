/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BlobRecordMapper.java
 * $Id$
 */

package su.svn.api.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.ResourceBlobRecord;
import su.svn.api.models.dto.UpdateBlobRecord;

@Mapper(componentModel = "cdi")
public interface BlobRecordMapper extends DateTimeMapper {

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @Mapping(target = "type", constant = "Blob")
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "sequenceId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "lastChangedTime", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "localChange", ignore = true)
    @Mapping(target = "json", ignore = true)
    @Mapping(target = "texts", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "html", ignore = true)
    @Mapping(target = "link", ignore = true)
    @Mapping(target = "markdown", ignore = true)
    @Mapping(target = "value", ignore = true)
    @Mapping(target = "vector", ignore = true)
    @Mapping(target = "xml", ignore = true)
    PostRecord toEntity(ResourceBlobRecord record);

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @Mapping(target = "type", constant = "Blob")
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "sequenceId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "lastChangedTime", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "localChange", ignore = true)
    @Mapping(target = "json", ignore = true)
    @Mapping(target = "texts", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "html", ignore = true)
    @Mapping(target = "link", ignore = true)
    @Mapping(target = "markdown", ignore = true)
    @Mapping(target = "value", ignore = true)
    @Mapping(target = "vector", ignore = true)
    @Mapping(target = "xml", ignore = true)
    PostRecord toEntity(UpdateBlobRecord record);

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    ResourceBlobRecord toResource(PostRecord postRecord);
}
