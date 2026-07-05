/*
 * This file was last modified at 2026.05.22 19:39 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecordMapper.java
 * $Id$
 */

package su.svn.api.services.mappers;

import org.mapstruct.*;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.ResourceVectorRecord;
import su.svn.api.models.dto.UpdateVectorRecord;

/**
 * MapStruct mapper for converting vector DTOs and views into {@link PostRecord}.
 *
 * <p>This mapper centralizes transformations between:
 * DTO objects, entity models, and API resource representations.</p>
 *
 * <h2>Mapping Rules</h2>
 * <ul>
 *     <li>Several persistence-only fields are ignored</li>
 *     <li>{@code userName} is always initialized with {@code root}</li>
 *     <li>Unsupported enum values are mapped to {@code null}</li>
 * </ul>
 */
@Mapper(componentModel = "cdi")
public interface VectorRecordMapper extends DateTimeMapper {

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "type", constant = "Vector")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "aHref", source = "aHref")
    @Mapping(target = "vector", source = "vector")
    @Mapping(target = "postAt", source = "postAt")
    @Mapping(target = "refreshAt", source = "refreshAt")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "flags", source = "flags")
    @Mapping(target = "tags", source = "tags")
    PostRecord toEntity(ResourceVectorRecord record);

    /**
     * Converts an update DTO into a {@link PostRecord} entity.
     *
     * @param record update DTO
     * @return mapped entity
     */
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "type", constant = "Vector")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "aHref", source = "aHref")
    @Mapping(target = "vector", source = "vector")
    @Mapping(target = "postAt", source = "postAt")
    @Mapping(target = "refreshAt", source = "refreshAt")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "flags", source = "flags")
    @Mapping(target = "tags", source = "tags")
    PostRecord toEntity(UpdateVectorRecord record);

    /**
     * Converts a {@link PostRecord} entity into a resource DTO.
     *
     * @param postRecord source entity
     * @return mapped resource DTO
     */
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    ResourceVectorRecord toResource(PostRecord postRecord);
}
