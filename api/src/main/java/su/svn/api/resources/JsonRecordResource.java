/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecordResource.java
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
import su.svn.api.models.dto.NewJsonRecord;
import su.svn.api.models.dto.ResourceJsonRecord;
import su.svn.api.models.dto.UpdateJsonRecord;
import su.svn.api.services.domain.JsonRecordDataService;
import su.svn.api.services.schedulers.RecordSchedulerService;

import java.util.UUID;

/**
 * REST resource for managing JSON records.
 *
 * <p>This resource exposes CRUD operations for JSON records and delegates
 * the business logic to {@link JsonRecordDataService}. After each successful
 * mutation (create, update, delete), it triggers {@link RecordSchedulerService}.
 *
 * <p>All endpoints are protected with {@link RolesAllowed} and require the "USER" role.
 *
 * <p>Reactive behavior:
 * <ul>
 *     <li>All methods return {@link Uni} and are non-blocking</li>
 *     <li>Scheduler trigger is executed as a side-effect using {@code onItem().invoke(...)}</li>
 * </ul>
 *
 * <p>Endpoints:
 * <ul>
 *     <li>{@code POST /record/json} — create a new record</li>
 *     <li>{@code DELETE /record/json/{id}} — delete a record</li>
 *     <li>{@code PUT /record/json} — update a record</li>
 * </ul>
 */
@Path(ResourcePath.RECORD + "/json")
public class JsonRecordResource {

    @Inject
    JsonRecordDataService service;

    @Inject
    RecordSchedulerService schedulerService;

    @APIResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = ResourceJsonRecord.class
                    )
            )
    )
    @APIResponse(ref = "500Error")
    @RolesAllowed("USER")
    @Operation(summary = "Create JSON record")
    @POST
    @Path(ResourcePath.NONE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<ResourceJsonRecord>> create(@Valid NewJsonRecord entry) {
        return service.post(entry)
                .map(record ->
                        RestResponse.ResponseBuilder
                                .create(Response.Status.CREATED, record)
                                .build()
                )
                .onItem()
                .invoke(() -> schedulerService.fire(true));
    }

    @APIResponse(ref = "204NoCont")
    @APIResponse(ref = "500Error")
    @RolesAllowed("USER")
    @Operation(summary = "Delete JSON record")
    @DELETE
    @Path(ResourcePath.ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> delete(UUID id) {
        return service.delete(id)
                .map(resourceJsonRecord ->
                        Response.status(Response.Status.NO_CONTENT).build()
                )
                .onItem()
                .invoke(() -> schedulerService.fire(true));
    }

    @APIResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = ResourceJsonRecord.class
                    )
            )
    )
    @APIResponse(ref = "500Error")
    @RolesAllowed("USER")
    @Operation(summary = "Update JSON record")
    @PUT
    @Path(ResourcePath.NONE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<ResourceJsonRecord>> update(@Valid UpdateJsonRecord entry) {
        return service.put(entry)
                .map(record ->
                        RestResponse.ResponseBuilder
                                .ok(record, MediaType.APPLICATION_JSON)
                                .build()
                )
                .onItem()
                .invoke(() -> schedulerService.fire(true));
    }
}
