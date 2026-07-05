/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecordSyncTrigger.java
 * $Id$
 */

package su.svn.api.services.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import su.svn.api.models.dto.ResourceJsonRecord;
import su.svn.api.services.schedulers.RecordSchedulerService;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Reactive synchronization trigger for JSON record operations.
 *
 * <p>
 * This component is responsible for notifying the
 * {@link RecordSchedulerService} whenever a JSON record
 * is created, updated, or deleted.
 * </p>
 *
 * <p>
 * The trigger is typically used as a reactive side-effect
 * handler in Mutiny pipelines via {@code Uni.onItem().invoke(...)}.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Trigger record synchronization scheduling</li>
 *     <li>Log synchronization events</li>
 *     <li>Support triggering by resource object or record identifier</li>
 * </ul>
 *
 * @see RecordSchedulerService
 * @see Consumer
 */
@ApplicationScoped
public class JsonRecordSyncTrigger implements Consumer<ResourceJsonRecord> {

    private static final Logger LOG = Logger.getLogger(JsonRecordSyncTrigger.class);

    /**
     * Scheduler service responsible for triggering record synchronization tasks.
     */
    @Inject
    RecordSchedulerService schedulerService;

    /**
     * Triggers synchronization after a successful JSON resource operation.
     *
     * <p>
     * This method schedules synchronization processing and logs
     * the affected resource.
     * </p>
     *
     * @param resource the JSON resource associated with the operation
     */
    @Override
    public void accept(ResourceJsonRecord resource) {
        schedulerService.fire(true);
        LOG.infof("resource: %s", resource);
    }

    /**
     * Triggers synchronization using a record identifier.
     *
     * <p>
     * Intended primarily for delete operations where the full
     * resource object is no longer available.
     * </p>
     *
     * @param id identifier of the affected blob record
     */
    public void accept(UUID id) {
        schedulerService.fire(true);
        LOG.infof("resource.id: %s", id);
    }
}