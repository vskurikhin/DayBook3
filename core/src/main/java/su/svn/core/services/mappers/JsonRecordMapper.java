/*
 * This file was last modified at 2026.05.22 09:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecordMapper.java
 * $Id$
 */

package su.svn.core.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import su.svn.core.domain.entities.JsonRecord;
import su.svn.core.models.dto.NewJsonRecord;
import su.svn.core.models.dto.ResourceJsonRecord;
import su.svn.core.models.dto.UpdateJsonRecord;

/**
 * Mapper for converting between {@link JsonRecord} and DTOs.
 *
 * <p>Uses MapStruct for automatic mapping between entities and
 * resource representations.</p>
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = {BaseRecordMapper.class}
)
public interface JsonRecordMapper extends TagMapper {
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @Mapping(source = "baseRecord.parentId", target = "parentId")
    @Mapping(source = "baseRecord.postAt", target = "postAt")
    @Mapping(source = "baseRecord.refreshAt", target = "refreshAt")
    @Mapping(source = "baseRecord.tags", target = "tags")
    @Mapping(source = "baseRecord.title", target = "title")
    @Mapping(source = "baseRecord.aHref", target = "aHref")
    ResourceJsonRecord toResource(JsonRecord record);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "refreshAt", ignore = true)
    ResourceJsonRecord toResource(NewJsonRecord record);

    @Mapping(target = "userName", ignore = true)
    ResourceJsonRecord toResource(UpdateJsonRecord record);

    @Mapping(target = "baseRecord", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "localChange", ignore = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "id", target = "baseRecord.id")
    @Mapping(source = "parentId", target = "baseRecord.parentId")
    @Mapping(source = "postAt", target = "baseRecord.postAt")
    @Mapping(source = "refreshAt", target = "baseRecord.refreshAt")
    @Mapping(source = "tags", target = "baseRecord.tags")
    @Mapping(source = "title", target = "baseRecord.title")
    @Mapping(source = "aHref", target = "baseRecord.aHref")
    JsonRecord toEntity(ResourceJsonRecord record);
}
