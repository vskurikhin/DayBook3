/*
 * This file was last modified at 2026.06.29 16:59 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * EntityModelResourceRecordMapper.java
 * $Id$
 */

package su.svn.api.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import su.svn.api.domain.entities.PostRecord;
import su.svn.lib.models.dto.EntityModelResourceRecordView;

@Mapper(componentModel = "cdi")
public interface EntityModelResourceRecordMapper extends DateTimeMapper {
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "sequenceId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "localChange", ignore = true)
    PostRecord toEntity(EntityModelResourceRecordView recordView);
}
