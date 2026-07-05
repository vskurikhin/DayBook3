/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * SetRecordResource.java
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
import su.svn.api.models.dto.NewSetRecord;
import su.svn.api.models.dto.ResourceSetRecord;
import su.svn.api.models.dto.UpdateSetRecord;
import su.svn.api.services.domain.SetRecordDataService;

import java.util.UUID;

/**
 * Reactive REST resource for managing set records.
 *
 * <p>
 * Provides endpoints for creating, updating, and deleting
 * set-based records within the system.
 * </p>
 *
 * <p>
 * All operations are asynchronous and return {@link Uni}
 * responses using the Mutiny reactive programming model.
 * </p>
 *
 * <p>
 * After successful modifications, the record scheduler
 * is triggered to synchronize record state changes.
 * </p>
 */
@Path(ResourcePath.RECORD + "/set")
public class SetRecordResource {

    /**
     * Service responsible for set record business logic.
     */
    @Inject
    SetRecordDataService service;

    /**
     * Creates a new set record.
     *
     * @param entry DTO containing data for the new set record
     * @return asynchronous HTTP response containing the created record
     * with status {@code 201 Created}
     */
    @APIResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = ResourceSetRecord.class
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
    public Uni<RestResponse<ResourceSetRecord>> create(@Valid NewSetRecord entry) {
        return service.post(entry)
                .map(record ->
                        RestResponse.ResponseBuilder
                                .create(Response.Status.CREATED, record)
                                .build()
                );
    }

    /**
     * Deletes an existing set record.
     *
     * <p>
     * Performs a logical deletion operation and triggers
     * record synchronization.
     * </p>
     *
     * @param id unique identifier of the record
     * @return asynchronous HTTP response with status
     * {@code 204 No Content}
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
     * Updates an existing set record.
     *
     * @param entry DTO containing updated record data
     * @return asynchronous HTTP response containing the updated record
     * with status {@code 200 OK}
     */
    @APIResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = ResourceSetRecord.class
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
    public Uni<RestResponse<ResourceSetRecord>> update(@Valid UpdateSetRecord entry) {
        return service.put(entry)
                .map(record ->
                        RestResponse.ResponseBuilder
                                .ok(record, MediaType.APPLICATION_JSON)
                                .build()
                );
    }
}
