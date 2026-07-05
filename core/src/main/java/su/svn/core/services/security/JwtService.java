/*
 * This file was last modified at 2026.05.08 11:39 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JwtService.java
 * $Id$
 */

package su.svn.core.services.security;

import java.util.Set;

/**
 * Service for working with JWT tokens.
 *
 * <p>
 * Provides functionality for:
 * </p>
 * <ul>
 *     <li>Extracting username from token</li>
 *     <li>Extracting user groups</li>
 *     <li>Token validation</li>
 * </ul>
 */
public interface JwtService {

    /**
     * Extracts username from JWT token.
     *
     * @param token JWT token
     * @return username
     */
    String extractUserName(String token);

    /**
     * Extracts user groups from JWT token.
     *
     * @param token JWT token
     * @return user groups
     */
    Set<String> extractGroups(String token);

    /**
     * Validates JWT token.
     *
     * @param token JWT token
     * @return true if token is valid
     */
    boolean isTokenValid(String token);

    /**
     * Validates JWT token with user principal name.
     *
     * @param token JWT token
     * @param upn   user principal name
     * @return true if token is valid
     */
    boolean isTokenValid(String token, String upn);
}