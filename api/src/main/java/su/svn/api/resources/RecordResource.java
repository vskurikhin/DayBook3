/*
 * This file was last modified at 2026.07.01 22:56 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordResource.java
 * $Id$
 */

package su.svn.api.resources;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import su.svn.api.domain.enums.ResourcePath;
import su.svn.api.models.dto.RecordData;
import su.svn.api.models.dto.RecordDataPage;
import su.svn.api.services.domain.PostRecordDataSyncService;
import su.svn.api.services.mappers.PageRecordDataMapper;

import java.util.UUID;

/**
 * REST resource for accessing post records.
 *
 * <p>
 * This resource provides reactive REST endpoints for retrieving
 * post record data. It delegates data loading and synchronization
 * operations to {@link PostRecordDataSyncService} and converts
 * domain entities into API DTO representations using
 * {@link PageRecordDataMapper}.
 * </p>
 *
 * <p>
 * All operations are implemented using Mutiny reactive types
 * and return non-blocking {@link Uni} responses.
 * </p>
 *
 * <h2>Available endpoints</h2>
 *
 * <ul>
 *     <li>
 *         <b>GET /records/{id}</b> —
 *         Retrieves a single record by its unique identifier.
 *     </li>
 *     <li>
 *         <b>GET /records</b> —
 *         Retrieves a paginated list of records.
 *     </li>
 * </ul>
 *
 * <p>
 * The resource allows anonymous access using {@link PermitAll}.
 * </p>
 */
@Path(ResourcePath.RECORDS)
public class RecordResource {

    @Inject
    PageRecordDataMapper mapper;

    @Inject
    PostRecordDataSyncService service;

    /**
     * Retrieves a single record by its unique identifier.
     *
     * <p>
     * The method delegates record loading to
     * {@link PostRecordDataSyncService#readPostRecord(UUID)}
     * and converts the retrieved entity into
     * {@link RecordData} using {@link PageRecordDataMapper}.
     * </p>
     *
     * <p>
     * The operation is executed asynchronously and returns a reactive
     * {@link Uni} that emits the requested record when available.
     * </p>
     *
     * @param id unique identifier of the requested record
     *
     * @return a {@link Uni} emitting the requested {@link RecordData}
     *
     * @see PostRecordDataSyncService#readPostRecord(UUID)
     */
    @APIResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = RecordData.class
                    )
            )
    )
    @APIResponse(ref = "500Error")
    @Path(ResourcePath.ID)
    @PermitAll
    @Operation(summary = "Retrieves a single record by its unique identifier")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RecordData> record(UUID id) {
        return service.readPostRecord(id)
                .log("RecordResource record")
                .map(record -> mapper.toDto(record));
    }

    /**
     * Retrieves a paginated list of records.
     *
     * <p>
     * The method requests a page of records from
     * {@link PostRecordDataSyncService#readPage(int, byte)}
     * and maps the result into an API response DTO using
     * {@link PageRecordDataMapper}.
     * </p>
     *
     * <p>
     * Pagination is controlled by query parameters:
     * </p>
     *
     * <ul>
     *     <li><b>page</b> — zero-based page index</li>
     *     <li><b>size</b> — maximum number of records per page</li>
     * </ul>
     *
     * @param page zero-based page index
     * @param size maximum number of records returned in a single page
     *
     * @return a {@link Uni} emitting a paginated {@link RecordDataPage}
     *
     * @see PostRecordDataSyncService#readPage(int, byte)
     */
    @APIResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = RecordDataPage.class
                    )
            )
    )
    @APIResponse(ref = "500Error")
    @PermitAll
    @Operation(summary = "Get page with list of records")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RecordDataPage> page(@QueryParam("page") int page, @QueryParam("size") byte size) {
        return service.readPage(page, size)
                .log("RecordResource page")
                .map(postRecordPage -> mapper.toPage(postRecordPage));
    }
}
