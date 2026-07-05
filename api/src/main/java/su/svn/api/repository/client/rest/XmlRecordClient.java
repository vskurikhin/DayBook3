/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * XmlRecordClient.java
 * $Id$
 */

package su.svn.api.repository.client.rest;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import su.svn.api.domain.enums.ResourcePath;
import su.svn.api.models.dto.NewXmlRecord;
import su.svn.api.models.dto.ResourceXmlRecord;
import su.svn.api.models.dto.UpdateXmlRecord;

import java.util.UUID;

/**
 * Reactive REST client for remote XML record operations.
 *
 * <p>
 * This client communicates with the remote XML record API
 * exposed by the core service.
 * </p>
 *
 * <h2>Supported Operations</h2>
 * <ul>
 *     <li>Create XML records</li>
 *     <li>Update XML records</li>
 *     <li>Delete XML records</li>
 * </ul>
 *
 * <p>
 * All operations are asynchronous and implemented using
 * Mutiny {@link Uni}.
 * </p>
 *
 * <h2>Headers</h2>
 * <ul>
 *     <li>{@code Authorization} — security token</li>
 *     <li>{@code X-Request-ID} — request tracing identifier</li>
 * </ul>
 *
 * @see NewXmlRecord
 * @see UpdateXmlRecord
 * @see ResourceXmlRecord
 * @see io.smallrye.mutiny.Uni
 */
@Path("/core/api/v2/xml-record")
@RegisterRestClient
public interface XmlRecordClient {

    /**
     * Deletes an XML record by identifier.
     *
     * @param authorization authorization token
     * @param requestId request correlation identifier
     * @param id XML record identifier
     * @return completion notification
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
     * Creates a new XML record.
     *
     * @param authorization authorization token
     * @param requestId request correlation identifier
     * @param newXmlRecord XML creation payload
     * @return created XML resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResourceXmlRecord> post(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            NewXmlRecord newXmlRecord
    );

    /**
     * Updates an existing XML record.
     *
     * @param authorization authorization token
     * @param requestId request correlation identifier
     * @param updateXmlRecord XML update payload
     * @return updated XML resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResourceXmlRecord> put(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            UpdateXmlRecord updateXmlRecord
    );
}
