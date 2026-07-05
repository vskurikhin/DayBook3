/*
 * This file was last modified at 2026.05.22 19:39 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * XmlRecordMapper.java
 * $Id$
 */

package su.svn.api.services.mappers;

import org.mapstruct.*;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.ResourceXmlRecord;
import su.svn.api.models.dto.UpdateXmlRecord;

/**
 * MapStruct mapper for converting XML DTOs into {@link PostRecord} entities
 * and resource DTO representations.
 *
 * <p>
 * This mapper centralizes transformation logic between:
 * </p>
 * <ul>
 *     <li>{@link UpdateXmlRecord}</li>
 *     <li>{@link PostRecord}</li>
 *     <li>{@link ResourceXmlRecord}</li>
 * </ul>
 *
 * <h2>Mapping Rules</h2>
 * <ul>
 *     <li>Only explicitly declared fields are mapped</li>
 *     <li>Unmapped target fields are ignored by default</li>
 *     <li>Unsupported enum values are converted to {@code null}</li>
 * </ul>
 *
 * <h2>Supported Transformations</h2>
 * <ul>
 *     <li>Update DTO → entity</li>
 *     <li>Entity → resource DTO</li>
 * </ul>
 *
 * @see PostRecord
 * @see UpdateXmlRecord
 * @see ResourceXmlRecord
 */
@Mapper(componentModel = "cdi")
public interface XmlRecordMapper extends DateTimeMapper {

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "type", constant = "Xml")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "aHref", source = "aHref")
    @Mapping(target = "xml", source = "xml")
    @Mapping(target = "postAt", source = "postAt")
    @Mapping(target = "refreshAt", source = "refreshAt")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "flags", source = "flags")
    @Mapping(target = "tags", source = "tags")
    PostRecord toEntity(ResourceXmlRecord record);

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "type", constant = "Xml")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "aHref", source = "aHref")
    @Mapping(target = "xml", source = "xml")
    @Mapping(target = "postAt", source = "postAt")
    @Mapping(target = "refreshAt", source = "refreshAt")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "flags", source = "flags")
    @Mapping(target = "tags", source = "tags")
    PostRecord toEntity(UpdateXmlRecord record);

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
    ResourceXmlRecord toResource(PostRecord postRecord);
}
