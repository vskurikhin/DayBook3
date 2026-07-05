/*
 * This file was last modified at 2026.05.08 11:39 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * NotFoundExceptionMapper.java
 * $Id$
 */

package su.svn.api.resources.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper  implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
    }
}
