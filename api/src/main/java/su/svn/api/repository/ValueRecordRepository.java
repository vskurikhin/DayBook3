/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ValueRecordRepository.java
 * $Id$
 */

package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.MDC;
import su.svn.api.models.dto.NewValueRecord;
import su.svn.api.models.dto.ResourceValueRecord;
import su.svn.api.models.dto.UpdateValueRecord;
import su.svn.api.repository.client.rest.ValueRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.util.Objects;
import java.util.UUID;

import static su.svn.lib.Constants.REQUEST_ID;

/**
 * Repository responsible for interaction with the remote
 * value record REST service.
 *
 * <p>
 * Adds authorization and request tracing metadata
 * before delegating requests to the REST client.
 * </p>
 */
@ApplicationScoped
public class ValueRecordRepository {

    public static final String NONE = "NONE";

    @Inject
    @RestClient
    ValueRecordClient client;

    @Inject
    SecurityContextPrincipalHelper principalHelper;

    /**
     * Deletes a value record.
     *
     * @param id identifier of the record to delete
     * @return asynchronous completion signal
     */
    public Uni<Void> delete(UUID id) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.delete(authorization, requestId, id);
    }

    /**
     * Creates a new value record.
     *
     * @param newRecord DTO containing new record data
     * @return asynchronously created value record resource
     */
    public Uni<ResourceValueRecord> post(NewValueRecord newRecord) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.post(authorization, requestId, newRecord);
    }

    /**
     * Updates an existing value record.
     *
     * @param updateRecord DTO containing updated record data
     * @return asynchronously updated value record resource
     */
    public Uni<ResourceValueRecord> put(UpdateValueRecord updateRecord) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.put(authorization, requestId, updateRecord);
    }
}
