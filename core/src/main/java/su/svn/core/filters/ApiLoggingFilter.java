/*
 * This file was last modified at 2026.05.08 11:39 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ApiLoggingFilter.java
 * $Id$
 */

package su.svn.core.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import su.svn.core.domain.entities.UserName;
import su.svn.core.servlet.GzipCountingResponseWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static su.svn.lib.Constants.REQUEST_ID;

/**
 * Servlet filter responsible for:
 * <ul>
 *     <li>Generating or propagating request id</li>
 *     <li>Logging HTTP request information</li>
 *     <li>Logging HTTP response metadata</li>
 *     <li>Masking sensitive request headers and parameters</li>
 *     <li>Calculating raw and gzip response sizes</li>
 * </ul>
 *
 * <p>
 * The filter stores request identifier in MDC under key {@code REQUEST_ID}
 * for correlation logging.
 * </p>
 *
 * <p>
 * Sensitive data masking:
 * </p>
 * <ul>
 *     <li>{@code Authorization} header is masked</li>
 *     <li>{@code password} request parameter is masked</li>
 * </ul>
 */
@Slf4j
public class ApiLoggingFilter implements Filter {

    /**
     * Default username used when authentication is absent.
     */
    public static final String GUEST = "guest";

    /**
     * Header name used for request identifier propagation.
     */
    private final String requestIdParamName;

    /**
     * Creates logging filter.
     *
     * @param requestIdParamName request id header name
     */
    public ApiLoggingFilter(String requestIdParamName) {
        this.requestIdParamName = requestIdParamName.toLowerCase();
    }

    /**
     * Logs HTTP request and response metadata.
     *
     * <p>
     * The method:
     * </p>
     * <ul>
     *     <li>Extracts request id from headers or generates a new one</li>
     *     <li>Stores request id in MDC</li>
     *     <li>Logs request metadata</li>
     *     <li>Wraps response for gzip/raw size calculation</li>
     *     <li>Logs response metadata</li>
     *     <li>Clears MDC after processing</li>
     * </ul>
     *
     * @param request servlet request
     * @param response servlet response
     * @param chain servlet filter chain
     */
    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            var httpServletRequest = (HttpServletRequest) request;
            var httpServletResponse = (HttpServletResponse) response;

            var headerMap = this.getTypeSafeHeaderMap(httpServletRequest);
            var requestMap = this.getTypeSafeRequestMap(httpServletRequest);
            var requestId = headerMap.containsKey(requestIdParamName)
                    ? headerMap.get(requestIdParamName)
                    : UUID.randomUUID().toString();
            MDC.put(REQUEST_ID, requestId);
            final StringBuilder logRequest = new StringBuilder("HTTP ").append(httpServletRequest.getMethod())
                    .append(" \"").append(httpServletRequest.getServletPath()).append("\"")
                    .append(", user_name=").append(getUserName())
                    .append(", parameters=").append(requestMap)
                    .append(", remote_address=").append(httpServletRequest.getRemoteAddr());
            log.info(logRequest.toString());
            var wrapped = new GzipCountingResponseWrapper(httpServletResponse);
            try {
                chain.doFilter(httpServletRequest, wrapped);
            } finally {
                log.info(
                        "HTTP RESPONSE, status={}, content_type={}, raw_size={}, gzip_size={}",
                        wrapped.getStatus(), wrapped.getContentType(), wrapped.getRawSize(), wrapped.getGzipSize()
                );
                MDC.clear();
            }
        } catch (Throwable a) {
            log.error(a.getMessage());
        }
    }

    /**
     * Creates type-safe request header map.
     *
     * <p>
     * Authorization header value is masked.
     * </p>
     *
     * @param request http servlet request
     * @return header map
     */
    private Map<String, String> getTypeSafeHeaderMap(HttpServletRequest request) {
        var typeSafeRequestMap = new HashMap<String, String>();
        var requestHeaderNames = request.getHeaderNames();
        while (requestHeaderNames.hasMoreElements()) {
            var requestHeaderName = requestHeaderNames.nextElement();
            String requestHeaderValue;
            if (requestHeaderName.equalsIgnoreCase("authorization")) {
                requestHeaderValue = "********";
            } else {
                requestHeaderValue = request.getHeader(requestHeaderName);
            }
            typeSafeRequestMap.put(requestHeaderName.toLowerCase(), requestHeaderValue);
        }
        return typeSafeRequestMap;
    }

    /**
     * Creates type-safe request parameter map.
     *
     * <p>
     * Password parameter value is masked.
     * </p>
     *
     * @param request http servlet request
     * @return request parameter map
     */
    private Map<String, String> getTypeSafeRequestMap(HttpServletRequest request) {
        var typeSafeRequestMap = new HashMap<String, String>();
        var requestParamNames = request.getParameterNames();
        while (requestParamNames.hasMoreElements()) {
            var requestParamName = requestParamNames.nextElement();
            String requestParamValue;
            if (requestParamName.equalsIgnoreCase("password")) {
                requestParamValue = "********";
            } else {
                requestParamValue = request.getParameter(requestParamName);
            }
            typeSafeRequestMap.put(requestParamName, requestParamValue);
        }
        return typeSafeRequestMap;
    }

    /**
     * Resolves current authenticated username.
     *
     * @return authenticated username or {@link #GUEST}
     */
    private static String getUserName() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return GUEST;
        }
        Object principal = authentication.getPrincipal();
        return switch (principal) {
            case UserName userName -> userName.userName();
            case User user -> user.getUsername();
            default -> GUEST;
        };
    }
}