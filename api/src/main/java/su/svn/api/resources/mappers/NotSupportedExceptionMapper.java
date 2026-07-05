/*
 * This file was last modified at 2026.05.08 11:39 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * NotSupportedExceptionMapper.java
 * $Id$
 */

package su.svn.api.resources.mappers;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotSupportedExceptionMapper implements ExceptionMapper<NotSupportedException> {
    @Override
    public Response toResponse(NotSupportedException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(String.format("HTTP 403 %s", exception.getMessage()))
                .build();
    }
}
