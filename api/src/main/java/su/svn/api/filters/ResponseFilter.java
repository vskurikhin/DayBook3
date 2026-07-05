/*
 * This file was last modified at 2026.05.08 09:18 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ResponseFilter.java
 * $Id$
 */

package su.svn.api.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.Objects;

import static su.svn.lib.Constants.REQUEST_ID;

@Provider
public class ResponseFilter implements ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(ResponseFilter.class);
    public static final String NONE = "NONE";

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        var requestId = org.jboss.logging.MDC.get(REQUEST_ID);
        var startTime = (Long) requestContext.getProperty("request-start-time");
        long duration = System.currentTimeMillis() - (startTime != null ? startTime : 0);
        LOG.infof("HTTP RESPONSE, status=%s, content_type=%s, length=%d, duration=%dms",
                responseContext.getStatus(),
                getMediaType(responseContext),
                responseContext.getLength(),
                duration
        );
        org.jboss.logging.MDC.clear();
        LOG.debugf("REQUEST_ID: %s - cleared", Objects.toString(requestId, NONE));
    }

    public static String getMediaType(ContainerResponseContext responseContext) {
        return responseContext.getMediaType() != null
                ? String.format("%s/%s",
                responseContext.getMediaType().getType(), responseContext.getMediaType().getSubtype())
                : NONE;
    }
}
