/*
 * This file was last modified at 2026.05.21 23:42 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * SetRecordService.java
 * $Id$
 */

package su.svn.core.services.domain;

import su.svn.core.models.dto.NewSetRecord;
import su.svn.core.models.dto.ResourceSetRecord;
import su.svn.core.models.dto.UpdateSetRecord;

import java.util.UUID;

/**
 * Service interface for managing {@code SetRecord} entities.
 *
 * <p>This service provides CRUD-like operations for set-based records,
 * including creation, update, retrieval, and logical deletion.</p>
 */
public interface SetRecordService {

    /**
     * Disables a record by identifier.
     *
     * <p>The record is not physically removed from the database.
     * Instead, the {@code enabled} flag is set to {@code false}.</p>
     *
     * @param id record identifier
     */
    void disable(UUID id);

    /**
     * Finds a record by identifier.
     *
     * @param id record identifier
     * @return found resource representation
     */
    ResourceSetRecord findById(UUID id);

    /**
     * Creates and stores a new set record.
     *
     * @param newRecord new record payload
     * @return created resource representation
     */
    ResourceSetRecord save(NewSetRecord newRecord);

    /**
     * Updates an existing set record.
     *
     * @param updateRecord update payload
     * @return updated resource representation
     */
    ResourceSetRecord update(UpdateSetRecord updateRecord);
}