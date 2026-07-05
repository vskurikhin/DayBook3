/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * XmlRecordResource.java
 * $Id$
 */

package su.svn.api.resources;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;
import su.svn.api.domain.enums.ResourcePath;
import su.svn.api.models.dto.NewXmlRecord;
import su.svn.api.models.dto.ResourceXmlRecord;
import su.svn.api.models.dto.UpdateXmlRecord;
import su.svn.api.services.domain.XmlRecordDataService;
import su.svn.api.services.schedulers.RecordSchedulerService;

import java.util.UUID;

/**
 * Reactive REST resource for XML record operations.
 *
 * <p>
 * This resource exposes secured HTTP endpoints for:
 * </p>
 * <ul>
 *     <li>Creating XML records</li>
 *     <li>Updating XML records</li>
 *     <li>Deleting XML records</li>
 * </ul>
 *
 * <p>
 * All operations are asynchronous and implemented using Mutiny {@link Uni}.
 * </p>
 *
 * <h2>Security</h2>
 * <p>
 * All endpoints require the {@code USER} role.
 * </p>
 *
 * <h2>Scheduler Integration</h2>
 * <p>
 * After successful create, update, or delete operations,
 * the {@link RecordSchedulerService} is notified to trigger
 * synchronization and background processing workflows.
 * </p>
 *
 * <h2>Media Types</h2>
 * <p>
 * The resource consumes and produces JSON payloads.
 * </p>
 *
 * @see XmlRecordDataService
 * @see RecordSchedulerService
 * @see io.smallrye.mutiny.Uni
 * @see jakarta.ws.rs.core.Response
 */
@Path(ResourcePath.RECORD + "/xml")
public class XmlRecordResource {

    @Inject
    XmlRecordDataService service;

    /**
     * Creates a new XML record.
     *
     * <p>
     * The request is delegated to {@link XmlRecordDataService}.
     * After successful creation the scheduler service is notified.
     * </p>
     *
     * @param entry DTO containing XML creation data
     * @return a {@link Uni} emitting HTTP 201 response
     * with the created XML resource
     */
    @APIResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = ResourceXmlRecord.class
                    )
            )
    )
    @APIResponse(ref = "500Error")
    @RolesAllowed("USER")
    @Operation(summary = "Create BLOB record")
    @POST
    @Path(ResourcePath.NONE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<ResourceXmlRecord>> create(@Valid NewXmlRecord entry) {
        return service.post(entry)
                .map(record ->
                        RestResponse.ResponseBuilder
                                .create(Response.Status.CREATED, record)
                                .build()
                );
    }

    /**
     * Deletes an XML record by identifier.
     *
     * <p>
     * After successful deletion the scheduler service is notified.
     * </p>
     *
     * @param id XML record identifier
     * @return a {@link Uni} emitting HTTP 204 response
     */
    @APIResponse(
            responseCode = "204",
            description = "No Content"
    )
    @APIResponse(ref = "500Error")
    @RolesAllowed("USER")
    @Operation(summary = "Delete BLOB record")
    @DELETE
    @Path(ResourcePath.ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> delete(UUID id) {
        return service.delete(id)
                .map(resourceJsonRecord ->
                        Response.status(Response.Status.NO_CONTENT).build()
                );
    }

    /**
     * Updates an existing XML record.
     *
     * <p>
     * The update request is delegated to the XML data service.
     * After successful update the scheduler service is notified.
     * </p>
     *
     * @param entry DTO containing XML update data
     * @return a {@link Uni} emitting HTTP 200 response
     * with the updated XML resource
     */
    @APIResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = ResourceXmlRecord.class
                    )
            )
    )
    @APIResponse(ref = "500Error")
    @RolesAllowed("USER")
    @Operation(summary = "Update BLOB record")
    @PUT
    @Path(ResourcePath.NONE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<ResourceXmlRecord>> update(@Valid UpdateXmlRecord entry) {
        return service.put(entry)
                .map(record ->
                        RestResponse.ResponseBuilder
                                .ok(record, MediaType.APPLICATION_JSON)
                                .build()
                );
    }
}
