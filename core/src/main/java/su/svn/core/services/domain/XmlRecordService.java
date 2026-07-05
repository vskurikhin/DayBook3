/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * XmlRecordService.java
 * $Id$
 */

package su.svn.core.services.domain;

import su.svn.core.models.dto.NewXmlRecord;
import su.svn.core.models.dto.ResourceXmlRecord;
import su.svn.core.models.dto.UpdateXmlRecord;

import java.util.UUID;

/**
 * Service interface for XML record management.
 *
 * <p>
 * Provides operations for:
 * <ul>
 *     <li>creating XML records</li>
 *     <li>updating XML records</li>
 *     <li>finding XML records</li>
 *     <li>logical deletion of XML records</li>
 * </ul>
 * </p>
 */
public interface XmlRecordService {

    /**
     * Disables an XML record by its identifier.
     *
     * @param id unique XML record identifier
     */
    void disable(UUID id);

    /**
     * Finds an active XML record by identifier.
     *
     * @param id unique XML record identifier
     * @return found XML record resource
     */
    ResourceXmlRecord findById(UUID id);

    /**
     * Creates and stores a new XML record.
     *
     * @param newRecord DTO containing XML record data
     * @return saved XML record resource
     */
    ResourceXmlRecord save(NewXmlRecord newRecord);


    /**
     * Updates an existing XML record.
     *
     * @param updateRecord DTO containing updated XML record data
     * @return updated XML record resource
     * @throws RuntimeException if access is denied
     */
    ResourceXmlRecord update(UpdateXmlRecord updateRecord);
}