/*
 * This file was last modified at 2026.07.01 23:05 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * PostRecordRepository.java
 * $Id$
 */

package su.svn.api.repository;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.Page;
import su.svn.api.services.mappers.PostRecordMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PostRecordRepository {

    private static final Logger LOG = Logger.getLogger(PostRecordRepository.class);

    public static int BATCH_SIZE = 1024;

    @Inject
    Mutiny.SessionFactory mutinySessionFactory;

    /**
     * Performs soft deletion of a record.
     *
     * <p>
     * The record is marked as disabled and its
     * {@code lastChangedTime} field is updated.
     * </p>
     *
     * @param id record identifier
     * @return reactive completion signal
     */
    @WithTransaction
    public Uni<Void> disable(@Nonnull UUID id) {
        return PostRecord.findByUUID(id)
                .onItem()
                .ifNotNull()
                .transform(entity -> {
                    entity.enabled(false);
                    entity.lastChangedTime(LocalDateTime.now());
                    return entity;
                })
                .flatMap(postRecord -> PanacheEntityBase.persist(postRecord))
                .onItem()
                .transformToUni(postRecord -> Uni.createFrom().voidItem())
                .ifNoItem()
                .after(PostRecord.TIMEOUT_DURATION)
                .fail()
                .onFailure()
                .recoverWithUni(Uni.createFrom().voidItem());
    }

    @WithSession
    public Uni<PostRecord> findById(UUID id) {
        return PostRecord.findByUUID(id);
    }

    /**
     * Retrieves the timestamp of the latest changed record.
     *
     * @return reactive result containing the latest change timestamp
     */
    @WithSession
    public Uni<LocalDateTime> findLastChangedTime() {
        return PostRecord.findLastChangedTimePostRecord()
                .replaceIfNullWith(new PostRecord())
                .map(PostRecord::lastChangedTime)
                .replaceIfNullWith(LocalDateTime.MIN);
    }

    @WithTransaction
    public Uni<List<PostRecord>> persistAll(List<PostRecord> records) {
        return mutinySessionFactory.withSession(session -> session
                .setBatchSize(BATCH_SIZE)
                .persistAll(records.toArray(new Object[0]))
        ).replaceWith(records);
    }

    @WithTransaction
    public Uni<List<PostRecord>> readIdIn(List<UUID> ids) {
        return PostRecord.readIdIn(ids);
    }

    @WithSession
    public Uni<Page<PostRecord>> readPage(int pageIndex, byte size) {
        var query = PostRecord.readEnabledOrderByPostAtAndRefreshAtDesc();
        var page = query.page(pageIndex, size);
        var unis = Uni.combine().all().unis(page.list(), page.pageCount(), PostRecord.countEnabled());
        return unis.asTuple().log("PostRecordRepository readPage unis").map(t2 ->
                new Page<>(t2.getItem1(), t2.getItem2(), pageIndex, t2.getItem1().size(), t2.getItem3())
        ).invoke(postRecordPage -> LOG.debugf("readPage(%d, %d): %s", pageIndex, size, postRecordPage.toString()));
    }

    /**
     * Updates mutable fields of an existing post record.
     *
     * @param postRecord source entity containing updated values
     * @return reactive result containing updated entity
     */
    @WithTransaction
    public Uni<PostRecord> update(@Nonnull PostRecord postRecord) {
        return PostRecord.findByUUID(postRecord.id())
                .onItem()
                .ifNotNull()
                .transform(entity -> {
                    PostRecordMapper.INSTANCE.update(entity, postRecord);
                    return entity;
                })
                .ifNoItem()
                .after(PostRecord.TIMEOUT_DURATION)
                .fail()
                .onFailure()
                .transform(IllegalStateException::new)
                .replaceIfNullWith(postRecord);
    }
}
