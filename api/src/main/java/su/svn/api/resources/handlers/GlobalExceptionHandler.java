/*
 * This file was last modified at 2026.05.08 11:39 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * GlobalExceptionHandler.java
 * $Id$
 */

package su.svn.api.resources.handlers;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {
    @Inject
    Logger log;

    @Override
    public Response toResponse(Throwable exception) {
        String sStackTrace = "";
        if (log.isDebugEnabled()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            sStackTrace = sw.toString(); // stack trace as a string
        }
        log.errorf(
                "Unhandled exception: %s message: %s %s",
                exception.getClass().getName(), exception.getMessage(), sStackTrace
        );
        return Response.status(500).entity("HTTP 500 Oops, something went wrong").build();
    }
}