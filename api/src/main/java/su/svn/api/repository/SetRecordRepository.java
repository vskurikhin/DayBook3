/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * SetRecordRepository.java
 * $Id$
 */

package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.MDC;
import su.svn.api.models.dto.NewSetRecord;
import su.svn.api.models.dto.ResourceSetRecord;
import su.svn.api.models.dto.UpdateSetRecord;
import su.svn.api.repository.client.rest.SetRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.util.Objects;
import java.util.UUID;

import static su.svn.lib.Constants.REQUEST_ID;

@ApplicationScoped
public class SetRecordRepository {

    public static final String NONE = "NONE";

    @Inject
    @RestClient
    SetRecordClient client;

    @Inject
    SecurityContextPrincipalHelper principalHelper;

    public Uni<Void> delete(UUID id) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.delete(authorization, requestId, id);
    }

    public Uni<ResourceSetRecord> post(NewSetRecord newJsonRecord) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.post(authorization, requestId, newJsonRecord);
    }

    public Uni<ResourceSetRecord> put(UpdateSetRecord updateJsonRecord) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.put(authorization, requestId, updateJsonRecord);
    }
}
