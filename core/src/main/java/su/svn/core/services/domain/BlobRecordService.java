/*
 * This file was last modified at 2026.05.21 16:48 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BlobRecordService.java
 * $Id$
 */

package su.svn.core.services.domain;

import su.svn.core.models.dto.NewBlobRecord;
import su.svn.core.models.dto.ResourceBlobRecord;
import su.svn.core.models.dto.UpdateBlobRecord;

import java.util.UUID;

/**
 * Service interface for managing blob records.
 * <p>
 * Provides operations for creating, updating, retrieving,
 * and disabling blob records.
 * </p>
 */
public interface BlobRecordService {

    /**
     * Disables a blob record by its identifier.
     * <p>
     * The record becomes unavailable for further retrieval
     * through active queries.
     * </p>
     *
     * @param id the unique identifier of the blob record
     */
    void disable(UUID id);

    /**
     * Finds an active blob record by its identifier.
     *
     * @param id the unique identifier of the blob record
     * @return the found blob record as a resource DTO
     * @throws su.svn.core.exceptions.CustomNotFoundException
     *         if the record does not exist or is disabled
     */
    ResourceBlobRecord findById(UUID id);


    /**
     * Creates and stores a new blob record.
     *
     * @param newRecord DTO containing data for the new blob record
     * @return the saved blob record as a resource DTO
     */
    ResourceBlobRecord save(NewBlobRecord newRecord);

    /**
     * Updates an existing blob record.
     * <p>
     * Only the owner of the record is allowed to update it.
     * </p>
     *
     * @param updateRecord DTO containing updated blob record data
     * @return the updated blob record as a resource DTO
     * @throws RuntimeException if access is denied
     */
    ResourceBlobRecord update(UpdateBlobRecord updateRecord);
}
