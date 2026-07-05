/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BlobRecordDataService.java
 * $Id$
 */

package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import su.svn.api.models.dto.NewBlobRecord;
import su.svn.api.models.dto.ResourceBlobRecord;
import su.svn.api.models.dto.UpdateBlobRecord;
import su.svn.api.repository.BlobRecordRepository;

import java.util.UUID;

/**
 * Reactive application service for blob record operations.
 *
 * <p>
 * This service coordinates blob record persistence operations
 * using {@link BlobRecordRepository} and triggers synchronization
 * events through {@link BlobRecordSyncTrigger}.
 * </p>
 *
 * <p>
 * All methods are implemented using Mutiny reactive primitives
 * and return {@link Uni} instances.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Create blob records</li>
 *     <li>Update blob records</li>
 *     <li>Delete blob records</li>
 *     <li>Trigger synchronization after successful operations</li>
 * </ul>
 *
 * @see BlobRecordRepository
 * @see BlobRecordSyncTrigger
 * @see Uni
 */
@ApplicationScoped
public class BlobRecordDataService {

    /**
     * Repository responsible for remote blob record operations.
     */
    @Inject
    BlobRecordRepository repository;

    @Inject
    BlobRecordSyncTrigger trigger;

    /**
     * Deletes a blob record by its identifier.
     *
     * <p>
     * The request is delegated to the remote blob repository.
     * </p>
     *
     * @param id the unique identifier of the blob record
     * @return a {@link Uni} emitting completion notification
     */
    public Uni<Void> delete(UUID id) {
        return repository.delete(id)
                .onItem()
                .invoke(unused -> trigger.accept(id));
    }

    /**
     * Creates a new blob record.
     *
     * <p>
     * The request is delegated to the remote blob repository.
     * </p>
     *
     * @param record DTO containing data for the new blob record
     * @return a {@link Uni} emitting the created blob resource
     */
    public Uni<ResourceBlobRecord> post(NewBlobRecord record) {
        return repository.post(record)
                .onItem()
                .invoke(trigger);
    }

    /**
     * Updates an existing markdown record.
     *
     * <p>
     * The request is delegated to the remote markdown repository.
     * </p>
     *
     * <p>
     * The resulting resource reflects the synchronized local post state.
     * </p>
     *
     * @param record DTO containing updated markdown data
     * @return a {@link Uni} emitting the updated markdown resource
     */
    public Uni<ResourceBlobRecord> put(UpdateBlobRecord record) {
        return repository.put(record)
                .onItem()
                .invoke(trigger);
    }
}
