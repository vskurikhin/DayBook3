/*
 * This file was last modified at 2026.05.21 16:48 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BlobRecordClient.java
 * $Id$
 */

package su.svn.api.repository.client.rest;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import su.svn.api.domain.enums.ResourcePath;
import su.svn.api.models.dto.NewBlobRecord;
import su.svn.api.models.dto.ResourceBlobRecord;
import su.svn.api.models.dto.UpdateBlobRecord;

import java.util.UUID;

@Path("/core/api/v2/blob-record")
@RegisterRestClient
public interface BlobRecordClient {
    @DELETE
    @Path("/" + ResourcePath.ID)
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<Void> delete(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            UUID id
    );

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResourceBlobRecord> post(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            NewBlobRecord newBlobRecord
    );

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResourceBlobRecord> put(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            UpdateBlobRecord updateBlobRecord
    );
}
