/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecordDataService.java
 * $Id$
 */

package su.svn.api.services.domain;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.NewVectorRecord;
import su.svn.api.models.dto.ResourceVectorRecord;
import su.svn.api.models.dto.UpdateVectorRecord;
import su.svn.api.repository.PostRecordRepository;
import su.svn.api.repository.VectorRecordRepository;
import su.svn.api.services.mappers.VectorRecordMapper;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Reactive application service for vector record operations.
 *
 * <p>
 * This service coordinates interactions between:
 * </p>
 * <ul>
 *     <li>{@link VectorRecordRepository} for remote vector record persistence</li>
 *     <li>{@link PostRecordRepository} for local post record synchronization</li>
 *     <li>{@link VectorRecordMapper} for DTO and entity transformations</li>
 * </ul>
 *
 * <p>
 * All operations are implemented using Mutiny reactive programming primitives
 * and return {@link Uni} instances.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Create vector records</li>
 *     <li>Update vector records</li>
 *     <li>Delete vector records</li>
 *     <li>Synchronize vector data with local post records</li>
 * </ul>
 *
 * <h2>Reactive Behavior</h2>
 * <p>
 * The service performs non-blocking asynchronous operations and combines
 * multiple reactive data sources where necessary.
 * </p>
 *
 * @see VectorRecordRepository
 * @see PostRecordRepository
 * @see VectorRecordMapper
 * @see io.smallrye.mutiny.Uni
 */
@ApplicationScoped
public class VectorRecordDataService {

    /**
     * Repository responsible for remote vector record operations.
     */
    @Inject
    VectorRecordRepository repository;

    @Inject
    VectorRecordSyncTrigger trigger;

    /**
     * Repository responsible for local post record persistence and synchronization.
     */
    @Inject
    PostRecordRepository postRecordRepository;

    /**
     * Deletes a vector record by its identifier.
     *
     * <p>
     * This operation performs two asynchronous actions in parallel:
     * </p>
     * <ul>
     *     <li>Deletes the vector record through the remote repository</li>
     *     <li>Disables the corresponding local post record</li>
     * </ul>
     *
     * <p>
     * The method completes successfully only when both operations finish.
     * </p>
     *
     * @param id the unique identifier of the vector record
     * @return a {@link Uni} emitting completion notification
     */
    public Uni<Void> delete(UUID id) {
        return repository.delete(id)
                .onItem()
                .invoke(unused -> trigger.accept(id));
    }

    /**
     * Creates a new vector record.
     *
     * <p>
     * The request is delegated to the remote vector repository.
     * </p>
     *
     * @param record DTO containing data for the new vector record
     * @return a {@link Uni} emitting the created vector resource
     */
    public Uni<ResourceVectorRecord> post(NewVectorRecord record) {
        return repository.post(record)
                .onItem()
                .invoke(trigger);
    }

    /**
     * Updates an existing vector record.
     *
     * <p>
     * The update process consists of:
     * </p>
     * <ol>
     *     <li>Updating the remote vector record</li>
     *     <li>Updating the corresponding local post record</li>
     *     <li>Mapping the updated entity back to a resource DTO</li>
     * </ol>
     *
     * <p>
     * The resulting resource reflects the synchronized local post state.
     * </p>
     *
     * @param record DTO containing updated vector data
     * @return a {@link Uni} emitting the updated vector resource
     */
    public Uni<ResourceVectorRecord> put(UpdateVectorRecord record) {
        return repository.put(record)
                .onItem()
                .invoke(trigger);
    }

    @WithTransaction
    public Uni<List<PostRecord>> persist(PostRecord record) {
        return postRecordRepository.persistAll(Collections.singletonList(record));
    }
}