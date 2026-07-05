/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecordService.java
 * $Id$
 */

package su.svn.core.services.domain;

import su.svn.core.models.dto.NewVectorRecord;
import su.svn.core.models.dto.ResourceVectorRecord;
import su.svn.core.models.dto.UpdateVectorRecord;

import java.util.UUID;

/**
 * Service interface for vector record management.
 * <p>
 * Provides operations for:
 * <ul>
 *     <li>creating vector records</li>
 *     <li>updating vector records</li>
 *     <li>finding vector records</li>
 *     <li>logical deletion of vector records</li>
 * </ul>
 * </p>
 */
public interface VectorRecordService {

    /**
     * Disables a vector record by its identifier.
     * <p>
     * The record becomes unavailable for further retrieval
     * through active queries.
     * </p>
     *
     * @param id the unique identifier of the vector record
     */
    void disable(UUID id);

    /**
     * Finds an active vector record by its identifier.
     *
     * @param id the unique identifier of the vector record
     * @return the found vector record as a resource DTO
     */
    ResourceVectorRecord findById(UUID id);

    /**
     * Creates and stores a new vector record.
     *
     * @param newRecord DTO containing data for the new vector record
     * @return the saved vector record as a resource DTO
     */
    ResourceVectorRecord save(NewVectorRecord newRecord);

    /**
     * Updates an existing vector record.
     * <p>
     * Only the owner of the record is allowed to update it.
     * </p>
     *
     * @param updateRecord DTO containing updated vector record data
     * @return the updated vector record as a resource DTO
     * @throws RuntimeException if access is denied
     */
    ResourceVectorRecord update(UpdateVectorRecord updateRecord);
}