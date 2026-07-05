/*
 * This file was last modified at 2026.05.03 19:13 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BaseRecordMapper.java
 * $Id$
 */

package su.svn.core.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.models.dto.ResourceBaseRecord;

/**
 * Mapper for converting between {@link BaseRecord} and DTOs.
 *
 * <p>Handles base record transformation logic.</p>
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface BaseRecordMapper {
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    ResourceBaseRecord toResource(BaseRecord record);

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "localChange", ignore = true)
    BaseRecord toEntity(ResourceBaseRecord record);
}
