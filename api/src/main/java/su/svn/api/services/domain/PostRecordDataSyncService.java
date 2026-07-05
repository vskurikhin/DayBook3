/*
 * This file was last modified at 2026.07.01 22:56 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * PostRecordDataSyncService.java
 * $Id$
 */

package su.svn.api.services.domain;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.Page;
import su.svn.api.repository.PostRecordRepository;
import su.svn.api.repository.RecordViewRepository;
import su.svn.api.services.mappers.ExistingPostRecordMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Reactive synchronization service for {@link PostRecord} entities.
 *
 * <p>
 * This service coordinates synchronization between the local
 * {@link PostRecordRepository} and external record sources
 * provided by {@link RecordViewRepository}.
 * </p>
 *
 * <p>
 * Synchronization is incremental and based on the latest
 * modification timestamp stored in the local repository.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Read paginated post record data</li>
 *     <li>Synchronize modified records from external sources</li>
 *     <li>Merge existing and incoming post records</li>
 *     <li>Persist synchronized records transactionally</li>
 * </ul>
 *
 * <p>
 * All operations are implemented using reactive Mutiny APIs.
 * </p>
 *
 * @see PostRecordRepository
 * @see RecordViewRepository
 * @see ExistingPostRecordMapper
 * @see Uni
 */
@ApplicationScoped
public class PostRecordDataSyncService {

    private static final Logger LOG = Logger.getLogger(PostRecordDataSyncService.class);

    @Inject
    PostRecordRepository postRecordRepository;

    @Inject
    ExistingPostRecordMapper existingPostRecordMapper;

    @Inject
    RecordViewRepository recordViewRepository;

    /**
     * Reads a single post record by its unique identifier.
     *
     * <p>
     * The method attempts to retrieve the record from both the local
     * {@link PostRecordRepository} and the external
     * {@link RecordViewRepository} concurrently.
     * The first successfully completed result is returned.
     * </p>
     *
     * <p>
     * This approach allows the service to combine data sources using
     * reactive execution without blocking the calling thread.
     * </p>
     *
     * @param id unique identifier of the requested post record
     * @return a {@link Uni} emitting the retrieved {@link PostRecord}
     */
    public Uni<PostRecord> readPostRecord(UUID id) {
        return Uni.combine()
                .any()
                .of(postRecordRepository.findById(id), recordViewRepository.readRecord(id));
    }

    /**
     * Reads a page of post records.
     *
     * <p>
     * The method requests data from both the local repository
     * and the external record view repository simultaneously.
     * The first successfully completed result is returned.
     * </p>
     *
     * @param pageIndex zero-based page index
     * @param size page size
     * @return a {@link Uni} emitting a page of post records
     */
    public Uni<Page<PostRecord>> readPage(int pageIndex, byte size) {
        return Uni.combine()
                .any()
                .of(postRecordRepository.readPage(pageIndex, size), recordViewRepository.readPage(pageIndex, size));
    }

    /**
     * Synchronizes modified post records from the external source.
     *
     * <p>
     * The synchronization process:
     * </p>
     * <ol>
     *     <li>Retrieves the latest local modification timestamp</li>
     *     <li>Loads records modified after that timestamp</li>
     *     <li>Merges existing local entities with incoming data</li>
     *     <li>Persists synchronized records transactionally</li>
     * </ol>
     *
     * @param pageIndex zero-based page index
     * @param size maximum number of records to synchronize
     * @return a {@link Uni} emitting synchronized post records
     */
    @WithTransaction
    public Uni<List<PostRecord>> sync(int pageIndex, int size) {
        return postRecordRepository.findLastChangedTime()
                .flatMap(fromTime ->
                        recordViewRepository.readList(pageIndex, size, fromTime)
                                .flatMap(this::syncPostRecords)
                );
    }

    /**
     * Synchronizes incoming post records with existing local entities.
     *
     * <p>
     * Existing records are updated using
     * {@link ExistingPostRecordMapper}, while new records
     * are inserted into the persistence layer.
     * </p>
     *
     * @param postRecords incoming post records
     * @return a {@link Uni} emitting persisted synchronized records
     */
    private Uni<List<PostRecord>> syncPostRecords(@Nonnull List<PostRecord> postRecords) {

        final Map<UUID, PostRecord> map = convertToMap(postRecords);

        return postRecordRepository.readIdIn(new ArrayList<>(map.keySet()))
                .flatMap(existingRecords -> {
                    existingRecords.forEach(existing -> {
                        PostRecord incoming = map.get(existing.id());

                        if (incoming != null) {
                            existingPostRecordMapper.updateExistingRecord(existing, incoming);
                            map.put(existing.id(), existing);
                            LOG.debugf("Updated existing post record: %s", existing.id());
                        }
                    });

                    return postRecordRepository.persistAll(List.copyOf(map.values()));
                });
    }

    /**
     * Converts a list of post records into a map indexed by identifier.
     *
     * @param postRecords source post records
     * @return map of post records indexed by UUID
     */
    private Map<UUID, PostRecord> convertToMap(@Nonnull List<PostRecord> postRecords) {
        return postRecords.stream()
                .collect(Collectors.toMap(PostRecord::id, Function.identity()));
    }
}
