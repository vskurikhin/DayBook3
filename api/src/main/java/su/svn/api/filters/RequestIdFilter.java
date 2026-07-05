/*
 * This file was last modified at 2026.05.07 17:43 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RequestIdFilter.java
 * $Id$
 */

package su.svn.api.filters;

import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.UUID;

import static su.svn.lib.Constants.REQUEST_ID;

@Provider
public class RequestIdFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(RequestIdFilter.class);
    public static final String X_FORWARDED_FOR = "X-Forwarded-For".toLowerCase();

    @ConfigProperty(name = "app.api.request-id-header-name")
    String requestIdHeaderName;

    @Inject
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty("request-start-time", System.currentTimeMillis());
        String requestId = requestContext.getHeaderString(requestIdHeaderName.toLowerCase());
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        org.jboss.logging.MDC.put(REQUEST_ID, requestId);
        LOG.infof("HTTP %s \"%s\", subject=%s, remote_address=%s, x_forwarded_for=%s",
                requestContext.getRequest().getMethod(),
                requestContext.getUriInfo().getPath(),
                getName(requestContext),
                request.remoteAddress().host(),
                requestContext.getHeaderString(X_FORWARDED_FOR)
        );
    }

    private static String getName(ContainerRequestContext requestContext) {
        return requestContext.getSecurityContext().getUserPrincipal() != null
                ? requestContext.getSecurityContext().getUserPrincipal().getName()
                : "NONE";
    }
}