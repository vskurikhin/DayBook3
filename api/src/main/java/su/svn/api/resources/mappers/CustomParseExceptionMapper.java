/*
 * This file was last modified at 2026.05.21 16:48 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * CustomParseExceptionMapper.java
 * $Id$
 */

package su.svn.api.resources.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import su.svn.api.models.exceptions.CustomParseException;

@Provider
public class CustomParseExceptionMapper implements ExceptionMapper<CustomParseException> {
    @Override
    public Response toResponse(CustomParseException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(toMessage(exception.getMessage()))
                .build();
    }

    public static String toMessage(String message) {
        return message.replaceFirst("^.*?(SR[A-Z]{3}\\d{5}.*)$", "HTTP 401 $1");
    }
}
