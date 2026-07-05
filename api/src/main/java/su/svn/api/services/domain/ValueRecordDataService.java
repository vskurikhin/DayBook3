/*
 * This file was last modified at 2026.05.29 20:51 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ValueRecordDataService.java
 * $Id$
 */

package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import su.svn.api.models.dto.NewValueRecord;
import su.svn.api.models.dto.ResourceValueRecord;
import su.svn.api.models.dto.UpdateValueRecord;
import su.svn.api.repository.ValueRecordRepository;

import java.util.UUID;

@ApplicationScoped
public class ValueRecordDataService {

    @Inject
    ValueRecordRepository repository;

    @Inject
    ValueRecordSyncTrigger trigger;

    public Uni<Void> delete(UUID id) {
        return repository.delete(id)
                .onItem()
                .invoke(unused -> trigger.accept(id));
    }

    public Uni<ResourceValueRecord> post(NewValueRecord record) {
        return repository.post(record)
                .onItem()
                .invoke(trigger);
    }

    public Uni<ResourceValueRecord> put(UpdateValueRecord record) {
        return repository.put(record)
                .onItem()
                .invoke(trigger);
    }
}
