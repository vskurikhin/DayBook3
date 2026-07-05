/*
 * This file was last modified at 2026.05.29 19:08 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecordDataService.java
 * $Id$
 */

package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import su.svn.api.models.dto.NewJsonRecord;
import su.svn.api.models.dto.ResourceJsonRecord;
import su.svn.api.models.dto.UpdateJsonRecord;
import su.svn.api.repository.JsonRecordRepository;

import java.util.UUID;


/**
 * Reactive application service responsible for JSON record operations.
 *
 * <p>
 * This service delegates JSON persistence operations to the remote
 * {@link JsonRecordRepository} and triggers asynchronous synchronization
 * workflows through {@link JsonRecordSyncTrigger}.
 * </p>
 *
 * <h2>Architecture</h2>
 * <p>
 * The service follows an eventual consistency model:
 * </p>
 * <ul>
 *     <li>The remote JSON service acts as the source of truth</li>
 *     <li>Local post record synchronization is performed asynchronously</li>
 *     <li>Synchronization is triggered through side-effect consumers</li>
 * </ul>
 *
 * <h2>Reactive Behavior</h2>
 * <p>
 * All operations are fully non-blocking and implemented using Mutiny
 * {@link io.smallrye.mutiny.Uni}.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Create JSON records</li>
 *     <li>Update JSON records</li>
 *     <li>Delete JSON records</li>
 *     <li>Trigger asynchronous synchronization processes</li>
 * </ul>
 *
 * @see JsonRecordRepository
 * @see JsonRecordSyncTrigger
 * @see io.smallrye.mutiny.Uni
 */
@ApplicationScoped
public class JsonRecordDataService {

    /**
     * Repository responsible for remote JSON record operations.
     */
    @Inject
    JsonRecordRepository repository;

    @Inject
    JsonRecordSyncTrigger trigger;

    /**
     * Deletes a JSON record by its identifier.
     *
     * <p>
     * The request is delegated to the remote JSON repository.
     * </p>
     *
     * @param id the unique identifier of the JSON record
     * @return a {@link Uni} emitting completion notification
     */
    public Uni<Void> delete(UUID id) {
        return repository.delete(id)
                .onItem()
                .invoke(unused -> trigger.accept(id));
    }

    /**
     * Creates a new JSON record.
     *
     * <p>
     * The request is delegated to the remote JSON repository.
     * </p>
     *
     * @param record DTO containing data for the new JSON record
     * @return a {@link Uni} emitting the created JSON resource
     */
    public Uni<ResourceJsonRecord> post(NewJsonRecord record) {
        return repository.post(record)
                .onItem()
                .invoke(trigger);
    }

    /**
     * Updates an existing JSON record.
     *
     * <p>
     * The request is delegated to the remote JSON repository.
     * </p>
     *
     * <p>
     * The resulting resource reflects the synchronized local post state.
     * </p>
     *
     * @param record DTO containing updated JSON data
     * @return a {@link Uni} emitting the updated JSON resource
     */
    public Uni<ResourceJsonRecord> put(UpdateJsonRecord record) {
        return repository.put(record)
                .onItem()
                .invoke(trigger);
    }
}
