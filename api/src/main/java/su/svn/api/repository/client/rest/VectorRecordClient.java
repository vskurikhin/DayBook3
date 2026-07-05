/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecordClient.java
 * $Id$
 */

package su.svn.api.repository.client.rest;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import su.svn.api.domain.enums.ResourcePath;
import su.svn.api.models.dto.NewVectorRecord;
import su.svn.api.models.dto.ResourceVectorRecord;
import su.svn.api.models.dto.UpdateVectorRecord;

import java.util.UUID;

/**
 * Reactive REST client for vector record operations.
 *
 * <p>
 * Provides remote access to CRUD operations
 * for vector records through the core service API.
 * </p>
 */
@Path("/core/api/v2/vector-record")
@RegisterRestClient
public interface VectorRecordClient {

    /**
     * Deletes a vector record by identifier.
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
     * Creates a new vector record.
     *
     * @param authorization authorization header value
     * @param requestId request identifier for tracing
     * @param newVectorRecord DTO containing new record data
     * @return asynchronously created resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResourceVectorRecord> post(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            NewVectorRecord newVectorRecord
    );

    /**
     * Updates an existing vector record.
     *
     * @param authorization authorization header value
     * @param requestId request identifier for tracing
     * @param updateVectorRecord DTO containing updated record data
     * @return asynchronously updated resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResourceVectorRecord> put(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            UpdateVectorRecord updateVectorRecord
    );
}
