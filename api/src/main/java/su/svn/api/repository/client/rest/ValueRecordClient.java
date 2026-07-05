/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ValueRecordClient.java
 * $Id$
 */

package su.svn.api.repository.client.rest;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import su.svn.api.domain.enums.ResourcePath;
import su.svn.api.models.dto.NewValueRecord;
import su.svn.api.models.dto.ResourceValueRecord;
import su.svn.api.models.dto.UpdateValueRecord;

import java.util.UUID;

/**
 * Reactive REST client for value record operations.
 *
 * <p>
 * Provides remote access to CRUD operations
 * for value records through the core service API.
 * </p>
 */
@Path("/core/api/v2/value-record")
@RegisterRestClient
public interface ValueRecordClient {

    /**
     * Deletes a value record by identifier.
     *
     * @param authorization authorization header value
     * @param requestId request identifier for tracing
     * @param id record identifier
     * @return asynchronous completion signal
     */
    @DELETE
    @Path("/" + ResourcePath.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<Void> delete(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            UUID id
    );

    /**
     * Creates a new value record.
     *
     * @param authorization authorization header value
     * @param requestId request identifier for tracing
     * @param newValueRecord DTO containing new record data
     * @return asynchronously created resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResourceValueRecord> post(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            NewValueRecord newValueRecord
    );

    /**
     * Updates an existing value record.
     *
     * @param authorization authorization header value
     * @param requestId request identifier for tracing
     * @param updateValueRecord DTO containing updated record data
     * @return asynchronously updated resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResourceValueRecord> put(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            UpdateValueRecord updateValueRecord
    );
}
