/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * MarkdownRecordClient.java
 * $Id$
 */

package su.svn.api.repository.client.rest;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import su.svn.api.domain.enums.ResourcePath;
import su.svn.api.models.dto.NewMarkdownRecord;
import su.svn.api.models.dto.ResourceMarkdownRecord;
import su.svn.api.models.dto.UpdateMarkdownRecord;

import java.util.UUID;

/**
 * Reactive REST client for markdown record operations.
 *
 * <p>
 * Provides communication with the remote core API
 * responsible for markdown record persistence.
 * </p>
 *
 * <p>
 * All operations are asynchronous and return
 * reactive {@link io.smallrye.mutiny.Uni} results.
 * </p>
 */
@Path("/core/api/v2/markdown-record")
@RegisterRestClient
public interface MarkdownRecordClient {

    /**
     * Deletes a markdown record.
     *
     * @param authorization bearer authorization token
     * @param requestId request correlation identifier
     * @param id record identifier
     * @return reactive completion signal
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
     * Creates a markdown record.
     *
     * @param authorization bearer authorization token
     * @param requestId request correlation identifier
     * @param newMarkdownRecord DTO for record creation
     * @return created markdown resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResourceMarkdownRecord> post(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            NewMarkdownRecord newMarkdownRecord
    );

    /**
     * Updates an existing markdown record.
     *
     * @param authorization bearer authorization token
     * @param requestId request correlation identifier
     * @param updateMarkdownRecord DTO with updated values
     * @return updated markdown resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResourceMarkdownRecord> put(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            UpdateMarkdownRecord updateMarkdownRecord
    );
}
