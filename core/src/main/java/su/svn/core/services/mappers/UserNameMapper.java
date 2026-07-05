/*
 * This file was last modified at 2026.03.27 14:01 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * UserNameMapper.java
 * $Id$
 */

package su.svn.core.services.mappers;

import org.mapstruct.Mapper;
import su.svn.core.domain.entities.UserName;
import su.svn.core.models.dto.NewUserName;

/**
 * Mapper for converting {@link UserName} entities from DTOs.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = {BaseRecordMapper.class}
)
public interface UserNameMapper {
    UserName toEntity(NewUserName record);
}
