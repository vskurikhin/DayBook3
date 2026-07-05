/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecordRepository.java
 * $Id$
 */

package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.MDC;
import su.svn.api.models.dto.NewVectorRecord;
import su.svn.api.models.dto.ResourceVectorRecord;
import su.svn.api.models.dto.UpdateVectorRecord;
import su.svn.api.repository.client.rest.VectorRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.util.Objects;
import java.util.UUID;

import static su.svn.lib.Constants.REQUEST_ID;

/**
 * Repository responsible for communication with the remote vector-record REST API.
 *
 * <p>This repository enriches outgoing requests with:
 * authorization headers and request correlation identifiers.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Create vector records</li>
 *     <li>Update vector records</li>
 *     <li>Delete vector records</li>
 * </ul>
 *
 * <p>All operations are implemented using reactive Mutiny {@link io.smallrye.mutiny.Uni}.</p>
 */
@ApplicationScoped
public class VectorRecordRepository {

    public static final String NONE = "NONE";

    @Inject
    @RestClient
    VectorRecordClient client;

    @Inject
    SecurityContextPrincipalHelper principalHelper;

    /**
     * Deletes a vector record.
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
     * Creates a new vector record.
     *
     * @param newRecord DTO containing new record data
     * @return asynchronously created vector record resource
     */
    public Uni<ResourceVectorRecord> post(NewVectorRecord newRecord) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.post(authorization, requestId, newRecord);
    }

    /**
     * Updates an existing vector record.
     *
     * @param updateRecord DTO containing updated record data
     * @return asynchronously updated vector record resource
     */
    public Uni<ResourceVectorRecord> put(UpdateVectorRecord updateRecord) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.put(authorization, requestId, updateRecord);
    }
}
