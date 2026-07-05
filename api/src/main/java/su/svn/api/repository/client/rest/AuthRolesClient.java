/*
 * This file was last modified at 2026.05.21 16:48 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * AuthRolesClient.java
 * $Id$
 */

package su.svn.api.repository.client.rest;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import su.svn.api.models.dto.SessionRolesData;

@Path("/auth/api/v2/session/roles")
@RegisterRestClient
public interface AuthRolesClient {
    @GET
    Uni<SessionRolesData> getUserHasRoles(@HeaderParam("Authorization") String authorization);
}
