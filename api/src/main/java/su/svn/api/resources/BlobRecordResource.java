/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BlobRecordResource.java
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
import su.svn.api.models.dto.NewBlobRecord;
import su.svn.api.models.dto.ResourceBlobRecord;
import su.svn.api.models.dto.UpdateBlobRecord;
import su.svn.api.services.domain.BlobRecordDataService;
import su.svn.api.services.domain.MarkdownRecordDataService;
import su.svn.api.services.schedulers.RecordSchedulerService;

import java.util.UUID;

/**
 * Reactive REST resource for blob record management.
 *
 * <p>
 * Provides REST endpoints for:
 * </p>
 * <ul>
 *     <li>creating blob records</li>
 *     <li>updating blob records</li>
 *     <li>deleting blob records</li>
 * </ul>
 *
 * <p>
 * All operations are implemented using reactive Mutiny {@link Uni} types
 * and return asynchronous HTTP responses.
 * </p>
 *
 * <h2>Security</h2>
 * <p>
 * All endpoints require the {@code USER} role.
 * </p>
 *
 * <h2>Scheduler Integration</h2>
 * <p>
 * After each successful modification operation, the
 * {@link RecordSchedulerService} is triggered to initiate
 * synchronization or refresh processing.
 * </p>
 *
 * @see MarkdownRecordDataService
 * @see RecordSchedulerService
 * @see io.smallrye.mutiny.Uni
 */
@Path(ResourcePath.RECORD + "/blob")
public class BlobRecordResource {

    /**
     * Service responsible for blob record business operations.
     */
    @Inject
    BlobRecordDataService service;

    /**
     * Creates a new blob record.
     *
     * <p>
     * Accepts blob record data in JSON format and returns
     * the created resource with HTTP status {@code 201 Created}.
     * </p>
     *
     * <p>
     * After successful creation, the scheduler service is triggered.
     * </p>
     *
     * @param entry DTO containing blob record creation data
     * @return reactive HTTP response containing the created blob resource
     */
    @APIResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = ResourceBlobRecord.class
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
    public Uni<RestResponse<ResourceBlobRecord>> create(@Valid NewBlobRecord entry) {
        return service.post(entry)
                .map(record ->
                        RestResponse.ResponseBuilder
                                .create(Response.Status.CREATED, record)
                                .build()
                );
    }

    /**
     * Deletes an existing blob record.
     *
     * <p>
     * Performs a logical deletion of the blob record identified by
     * the specified UUID and returns HTTP status {@code 204 No Content}.
     * </p>
     *
     * <p>
     * After successful deletion, the scheduler service is triggered.
     * </p>
     *
     * @param id unique identifier of the blob record
     * @return reactive HTTP response with no content
     */
    @APIResponse(ref = "204NoCont")
    @APIResponse(ref = "500Error")
    @RolesAllowed("USER")
    @Operation(summary = "Delete BLOB record")
    @DELETE
    @Path(ResourcePath.ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> delete(UUID id) {
        return service.delete(id)
                .map(unused ->
                        Response.status(Response.Status.NO_CONTENT).build()
                );
    }

    /**
     * Updates an existing blob record.
     *
     * <p>
     * Accepts updated blob record data in JSON format and returns
     * the updated resource with HTTP status {@code 200 OK}.
     * </p>
     *
     * <p>
     * After successful update, the scheduler service is triggered.
     * </p>
     *
     * @param entry DTO containing updated blob record data
     * @return reactive HTTP response containing the updated blob resource
     */
    @APIResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = ResourceBlobRecord.class
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
    public Uni<RestResponse<ResourceBlobRecord>> update(@Valid UpdateBlobRecord entry) {
        return service.put(entry)
                .map(record -> RestResponse.ResponseBuilder
                        .ok(record, MediaType.APPLICATION_JSON)
                        .build()
                );
    }
}
