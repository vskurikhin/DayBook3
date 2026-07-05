/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * MarkdownRecordService.java
 * $Id$
 */

package su.svn.core.services.domain;

import su.svn.core.models.dto.NewMarkdownRecord;
import su.svn.core.models.dto.ResourceMarkdownRecord;
import su.svn.core.models.dto.UpdateMarkdownRecord;

import java.util.UUID;

/**
 * Service interface for markdown record management.
 * <p>
 * Provides operations for:
 * <ul>
 *     <li>creating markdown records</li>
 *     <li>updating markdown records</li>
 *     <li>finding markdown records</li>
 *     <li>logical deletion of markdown records</li>
 * </ul>
 * </p>
 */
public interface MarkdownRecordService {

    /**
     * Disables a markdown record by its identifier.
     * <p>
     * The record becomes unavailable for further retrieval
     * through active queries.
     * </p>
     *
     * @param id the unique identifier of the markdown record
     */
    void disable(UUID id);

    /**
     * Finds an active markdown record by its identifier.
     *
     * @param id the unique identifier of the markdown record
     * @return the found markdown record as a resource DTO
     */
    ResourceMarkdownRecord findById(UUID id);

    /**
     * Creates and stores a new markdown record.
     *
     * @param newRecord DTO containing data for the new markdown record
     * @return the saved markdown record as a resource DTO
     */
    ResourceMarkdownRecord save(NewMarkdownRecord newRecord);

    /**
     * Updates an existing markdown record.
     * <p>
     * Only the owner of the record is allowed to update it.
     * </p>
     *
     * @param updateRecord DTO containing updated markdown record data
     * @return the updated markdown record as a resource DTO
     * @throws RuntimeException if access is denied
     */
    ResourceMarkdownRecord update(UpdateMarkdownRecord updateRecord);
}