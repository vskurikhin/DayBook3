/*
 * This file was last modified at 2026.07.03 12:04 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordSchedulerService.java
 * $Id$
 */

package su.svn.api.services.schedulers;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import su.svn.api.services.domain.PostRecordDataSyncService;

import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class RecordSchedulerService {

    private static final Logger LOG = Logger.getLogger(RecordSchedulerService.class);

    public static final int SYNC_SIZE = 2000;
    private final AtomicBoolean done = new AtomicBoolean(true);
    private final AtomicBoolean fire = new AtomicBoolean(false);

    @Inject
    PostRecordDataSyncService syncService;

    @Scheduled(every = "5s")
    Uni<Void> job() {
        if (!fire.get() || !done.compareAndSet(true, false)) {
            return Uni.createFrom().voidItem();
        }

        return syncPage(0)
                .eventually(() -> {
                    fire.set(false);
                    done.set(true);
                    return Uni.createFrom().voidItem();
                });
    }

    private Uni<Void> syncPage(int pageIndex) {
        return syncService.sync(pageIndex, SYNC_SIZE)
                .invoke(result ->
                        LOG.infof("page=%d size=%d", pageIndex, result.size())
                )
                .flatMap(result -> {
                    if (result.isEmpty()) {
                        return Uni.createFrom().voidItem();
                    }
                    return syncPage(pageIndex + 1);
                })
                .onFailure().invoke(t ->
                        LOG.errorf(t, "Synchronization failed on page %d", pageIndex)
                );
    }

    public void fire(boolean fire) {
        this.fire.set(fire);
    }
}
