/*
 * This file was last modified at 2026.05.29 19:08 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * SetRecordDataService.java
 * $Id$
 */

package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import su.svn.api.models.dto.NewSetRecord;
import su.svn.api.models.dto.ResourceSetRecord;
import su.svn.api.models.dto.UpdateSetRecord;
import su.svn.api.repository.SetRecordRepository;

import java.util.UUID;

/**
 * Reactive application service responsible for set of text record operations.
 *
 * <p>
 * This service delegates set of text persistence operations to the remote
 * {@link SetRecordRepository} and triggers asynchronous synchronization
 * workflows through {@link SetRecordSyncTrigger}.
 * </p>
 *
 * <h2>Architecture</h2>
 * <p>
 * The service follows an eventual consistency model:
 * </p>
 * <ul>
 *     <li>The remote set of text service acts as the source of truth</li>
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
 *     <li>Create set of text records</li>
 *     <li>Update set of text records</li>
 *     <li>Delete set of text records</li>
 *     <li>Trigger asynchronous synchronization processes</li>
 * </ul>
 *
 * @see SetRecordRepository
 * @see SetRecordSyncTrigger
 * @see io.smallrye.mutiny.Uni
 */
@ApplicationScoped
public class SetRecordDataService {

    /**
     * Repository responsible for remote set of text record operations.
     */
    @Inject
    SetRecordRepository repository;

    @Inject
    SetRecordSyncTrigger trigger;


    /**
     * Deletes a set of text record by its identifier.
     *
     * <p>
     * The request is delegated to the remote set of text repository.
     * </p>
     *
     * @param id the unique identifier of the set of text record
     * @return a {@link Uni} emitting completion notification
     */
    public Uni<Void> delete(UUID id) {
        return repository.delete(id)
                .onItem()
                .invoke(unused -> trigger.accept(id));
    }

    /**
     * Creates a new set of text record.
     *
     * <p>
     * The request is delegated to the remote set of text repository.
     * </p>
     *
     * @param record DTO containing data for the new set of text record
     * @return a {@link Uni} emitting the created set of text resource
     */
    public Uni<ResourceSetRecord> post(NewSetRecord record) {
        return repository.post(record)
                .onItem()
                .invoke(trigger);
    }

    /**
     * Updates an existing set of text record.
     *
     * <p>
     * The request is delegated to the remote set of text repository.
     * </p>
     *
     * <p>
     * The resulting resource reflects the synchronized local post state.
     * </p>
     *
     * @param record DTO containing updated set of text data
     * @return a {@link Uni} emitting the updated set of text resource
     */
    public Uni<ResourceSetRecord> put(UpdateSetRecord record) {
        return repository.put(record)
                .onItem()
                .invoke(trigger);
    }
}
