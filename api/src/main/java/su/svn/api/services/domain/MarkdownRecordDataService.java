/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * MarkdownRecordDataService.java
 * $Id$
 */

package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import su.svn.api.models.dto.NewMarkdownRecord;
import su.svn.api.models.dto.ResourceMarkdownRecord;
import su.svn.api.models.dto.UpdateMarkdownRecord;
import su.svn.api.repository.MarkdownRecordRepository;

import java.util.UUID;

/**
 * Reactive application service responsible for markdown record operations.
 *
 * <p>
 * This service delegates markdown persistence operations to the remote
 * {@link MarkdownRecordRepository} and triggers asynchronous synchronization
 * workflows through {@link MarkdownRecordSyncTrigger}.
 * </p>
 *
 * <h2>Architecture</h2>
 * <p>
 * The service follows an eventual consistency model:
 * </p>
 * <ul>
 *     <li>The remote markdown service acts as the source of truth</li>
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
 *     <li>Create markdown records</li>
 *     <li>Update markdown records</li>
 *     <li>Delete markdown records</li>
 *     <li>Trigger asynchronous synchronization processes</li>
 * </ul>
 *
 * @see MarkdownRecordRepository
 * @see MarkdownRecordSyncTrigger
 * @see io.smallrye.mutiny.Uni
 */
@ApplicationScoped
public class MarkdownRecordDataService {

    /**
     * Repository responsible for remote markdown record operations.
     */
    @Inject
    MarkdownRecordRepository repository;

    @Inject
    MarkdownRecordSyncTrigger trigger;

    /**
     * Deletes a markdown record by its identifier.
     *
     * <p>
     * The request is delegated to the remote markdown repository.
     * </p>
     *
     * @param id the unique identifier of the markdown record
     * @return a {@link Uni} emitting completion notification
     */
    public Uni<Void> delete(UUID id) {
        return repository.delete(id)
                .onItem()
                .invoke(unused -> trigger.accept(id));
    }

    /**
     * Creates a new markdown record.
     *
     * <p>
     * The request is delegated to the remote markdown repository.
     * </p>
     *
     * @param record DTO containing data for the new markdown record
     * @return a {@link Uni} emitting the created markdown resource
     */
    public Uni<ResourceMarkdownRecord> post(NewMarkdownRecord record) {
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
    public Uni<ResourceMarkdownRecord> put(UpdateMarkdownRecord record) {
        return repository.put(record)
                .onItem()
                .invoke(trigger);
    }
}