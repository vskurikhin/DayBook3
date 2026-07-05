/*
 * This file was last modified at 2026.06.29 16:59 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordViewMapper.java
 * $Id$
 */

package su.svn.core.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import su.svn.core.domain.entities.RecordView;
import su.svn.lib.models.dto.ResourceRecordView;

/**
 * Mapper for converting {@link su.svn.core.domain.entities.RecordView}
 * to {@link ResourceRecordView}.
 */
@Mapper(
        componentModel = "spring",
        uses = {BaseRecordMapper.class}
)
public interface RecordViewMapper {
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    ResourceRecordView toResource(RecordView record);
}
