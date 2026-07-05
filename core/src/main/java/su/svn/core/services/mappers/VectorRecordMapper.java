/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecordMapper.java
 * $Id$
 */

package su.svn.core.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import su.svn.core.domain.entities.VectorRecord;
import su.svn.core.models.dto.NewVectorRecord;
import su.svn.core.models.dto.ResourceVectorRecord;
import su.svn.core.models.dto.UpdateVectorRecord;

/**
 * Mapper for converting vector record entities and DTO models.
 *
 * <p>
 * Provides conversion methods between:
 * </p>
 * <ul>
 *     <li>{@link VectorRecord}</li>
 *     <li>{@link NewVectorRecord}</li>
 *     <li>{@link UpdateVectorRecord}</li>
 *     <li>{@link ResourceVectorRecord}</li>
 * </ul>
 *
 * <p>
 * Implemented automatically by MapStruct.
 * </p>
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = {BaseRecordMapper.class}
)
public interface VectorRecordMapper extends TagMapper {
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @Mapping(source = "baseRecord.parentId", target = "parentId")
    @Mapping(source = "baseRecord.postAt", target = "postAt")
    @Mapping(source = "baseRecord.refreshAt", target = "refreshAt")
    @Mapping(source = "baseRecord.tags", target = "tags")
    @Mapping(source = "baseRecord.title", target = "title")
    @Mapping(source = "baseRecord.aHref", target = "aHref")
    ResourceVectorRecord toResource(VectorRecord record);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "refreshAt", ignore = true)
    ResourceVectorRecord toResource(NewVectorRecord record);

    @Mapping(target = "userName", ignore = true)
    ResourceVectorRecord toResource(UpdateVectorRecord record);

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
    VectorRecord toEntity(ResourceVectorRecord record);
}
