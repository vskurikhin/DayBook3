/*
 * This file was last modified at 2026.07.03 12:04 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JwtAuthenticationFilter.java
 * $Id$
 */

package su.svn.core.filters;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import su.svn.core.services.domain.UserNameService;
import su.svn.core.services.security.JwtService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String ANONYMOUS = "anonymous";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String GUEST = "guest";
    public static final String GUEST_ROLE = "GUEST";
    public static final String HEADER_NAME = "Authorization";

    private final JwtService jwtService;
    private final UserNameService userNameService;

    @SneakyThrows
    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {

        // Получаем токен из заголовка
        var authHeader = request.getHeader(HEADER_NAME);
        if (ObjectUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            setSecurityContext(GUEST, ANONYMOUS, request, GUEST_ROLE);
            filterChain.doFilter(request, response);
            return;
        }

        // Обрезаем префикс и получаем имя пользователя из токена
        var jwt = authHeader.substring(BEARER_PREFIX.length());
        var upn = jwtService.extractUserName(jwt);
        var groups = jwtService.extractGroups(jwt);

        // Если токен валиден, то аутентифицируем пользователя
        if (jwtService.isTokenValid(jwt, upn)) {
            try {
                var userName = userNameService.findByUserName(upn);
                var roles = groups.toArray(new String[0]);
                setSecurityContext(userName.userName(), jwt, request, roles);
            } catch (UsernameNotFoundException ignore) {
                setSecurityContext(GUEST, jwt, request, GUEST_ROLE);
            }
        }
        filterChain.doFilter(request, response);
    }

    private static void setSecurityContext(String userName, String jwt, HttpServletRequest request, String... roles) {
        var user = User.withUsername(userName)
                .password(jwt)
                .roles(roles)
                .build();
        var context = SecurityContextHolder.createEmptyContext();
        var authToken = new UsernamePasswordAuthenticationToken(user, jwt, user.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }
}