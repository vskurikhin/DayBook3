/*
 * This file was last modified at 2026.07.01 22:56 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * PageRecordDataMapper.java
 * $Id$
 */

package su.svn.api.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.Page;
import su.svn.api.models.dto.RecordData;
import su.svn.api.models.dto.RecordDataPage;

@Mapper(componentModel = "cdi")
public interface PageRecordDataMapper {
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    RecordDataPage toPage(Page<PostRecord> record);

    RecordData toDto(PostRecord record);
}
