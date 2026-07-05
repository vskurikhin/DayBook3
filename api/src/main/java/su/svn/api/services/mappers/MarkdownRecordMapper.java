/*
 * This file was last modified at 2026.05.22 19:39 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * MarkdownRecordMapper.java
 * $Id$
 */

package su.svn.api.services.mappers;

import org.mapstruct.*;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.ResourceMarkdownRecord;
import su.svn.api.models.dto.UpdateMarkdownRecord;

/**
 * MapStruct mapper for converting markdown record DTOs
 * and {@link PostRecord} entities.
 *
 * <p>
 * Provides mapping operations for transforming
 * update DTOs into entities and entities into
 * API resource DTOs.
 * </p>
 */
@Mapper(componentModel = "cdi")
public interface MarkdownRecordMapper extends DateTimeMapper {

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "type", constant = "Text")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "aHref", source = "aHref")
    @Mapping(target = "markdown", source = "markdown")
    @Mapping(target = "postAt", source = "postAt")
    @Mapping(target = "refreshAt", source = "refreshAt")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "flags", source = "flags")
    @Mapping(target = "tags", source = "tags")
    PostRecord toEntity(ResourceMarkdownRecord record);

    /**
     * Converts an update DTO into a {@link PostRecord} entity.
     *
     * @param record update DTO
     * @return mapped entity
     */
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "type", constant = "Text")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "aHref", source = "aHref")
    @Mapping(target = "markdown", source = "markdown")
    @Mapping(target = "postAt", source = "postAt")
    @Mapping(target = "refreshAt", source = "refreshAt")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "flags", source = "flags")
    @Mapping(target = "tags", source = "tags")
    PostRecord toEntity(UpdateMarkdownRecord record);

    /**
     * Converts a {@link PostRecord} entity into a resource DTO.
     *
     * @param postRecord source entity
     * @return mapped resource DTO
     */
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    ResourceMarkdownRecord toResource(PostRecord postRecord);
}
