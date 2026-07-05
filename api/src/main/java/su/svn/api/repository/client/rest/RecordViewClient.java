/*
 * This file was last modified at 2026.07.01 22:56 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordViewClient.java
 * $Id$
 */

package su.svn.api.repository.client.rest;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import su.svn.api.models.dto.PagedModelEntityModelResourceRecordView;
import su.svn.lib.models.dto.EntityModelResourceRecordView;

import java.time.LocalDateTime;
import java.util.UUID;

@Path("/core/api/v2/records-view")
@RegisterRestClient
public interface RecordViewClient {
    @GET
    Uni<EntityModelResourceRecordView> getRecord(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            @QueryParam("id")UUID id
    );

    @GET
    Uni<PagedModelEntityModelResourceRecordView> getByPageIndexAndSizeAsUni(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            @QueryParam("page") int pageIndex,
            @QueryParam("size") int size,
            @QueryParam("sort") String sort
    );

    @GET
    Uni<PagedModelEntityModelResourceRecordView> getByPageIndexAndSizeAndFromTimeAsUni(
            @HeaderParam("Authorization") String authorization,
            @HeaderParam("X-Request-ID") String requestId,
            @QueryParam("page") int pageIndex,
            @QueryParam("size") int size,
            @QueryParam("sort") String sort,
            @QueryParam("fromTime") LocalDateTime fromTime,
            @QueryParam("withDisabled") Boolean withDisabled
    );
}
