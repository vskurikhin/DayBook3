/*
 * This file was last modified at 2026.05.07 14:57 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * SecurityContextPrincipalHelper.java
 * $Id$
 */

package su.svn.api.services.security;

import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.logging.Logger;

import java.util.Set;
import java.util.UUID;

/**
 * Helper component for building authorization headers and internal JWT tokens
 * based on the current {@link SecurityContext} and {@link io.quarkus.security.identity.SecurityIdentity}.
 *
 * <p>This class is responsible for:
 * <ul>
 *     <li>Extracting the current authenticated principal</li>
 *     <li>Rebuilding an internal JWT token signed with a shared secret</li>
 *     <li>Falling back to a "guest" token when no authenticated principal is available</li>
 * </ul>
 *
 * <p>Main behavior:
 * <ul>
 *     <li>If the current principal is an instance of {@link DefaultJWTCallerPrincipal}:
 *         <ul>
 *             <li>Issuer and subject are copied from the original token</li>
 *             <li>User name (UPN) is taken from {@link io.quarkus.security.identity.SecurityIdentity} attributes</li>
 *             <li>Roles are taken from {@link io.quarkus.security.identity.SecurityIdentity#getRoles()}</li>
 *             <li>JWT ID (jti) is reused from the original token</li>
 *         </ul>
 *     </li>
 *     <li>If no valid principal is found:
 *         <ul>
 *             <li>A fallback "guest" token is generated</li>
 *             <li>Uses predefined subject, username, and role</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <p>The generated token is signed using an HMAC secret configured via:
 * <pre>
 * application.external-jwt-verify-key-hmac
 * </pre>
 *
 * <p>The {@link #authorization()} method prepends a configured prefix (e.g., "Bearer")
 * to the generated token.
 *
 * <p><b>Important:</b>
 * <ul>
 *     <li>This class assumes HS256 symmetric signing</li>
 *     <li>It depends on {@link SecurityContext} and {@link io.quarkus.security.identity.SecurityIdentity}</li>
 *     <li>Custom identity attributes (e.g., UPN) must be set elsewhere (e.g., via augmentors)</li>
 * </ul>
 *
 * @see DefaultJWTCallerPrincipal
 * @see io.quarkus.security.identity.SecurityIdentity
 * @see jakarta.ws.rs.core.SecurityContext
 */
@ApplicationScoped
public class SecurityContextPrincipalHelper {

    private static final Logger LOG = Logger.getLogger(SecurityContextPrincipalHelper.class);
    private static final String GUEST_SUBJECT_UUID = "0f9233b1-7390-515d-9787-175006338642";

    @ConfigProperty(name = "app.external-jwt-api-key-prefix")
    String apiKeyPrefix;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String mpJwtVerifyIssuer;

    @ConfigProperty(name = "app.external-jwt-verify-key-hmac")
    String secret;

    @Inject
    io.quarkus.security.identity.SecurityIdentity identity;

    public String authorization() {
        return apiKeyPrefix + " " + this.insideToken();
    }

    public String insideToken() {
        try {
            var identityPrincipal = identity.getPrincipal();
            if (identityPrincipal instanceof DefaultJWTCallerPrincipal principal) {
                var extToken1 = Jwt.issuer(principal.getIssuer())
                        .subject(principal.getSubject())
                        .upn(identity.getAttribute(CustomIdentityAugmentor.UPN))
                        .groups(identity.getRoles())
                        .claim(Claims.jti, principal.getTokenID())
                        .signWithSecret(secret);
                LOG.debugf(
                        "subject=%s, JTI=%s, issuer=%s",
                        principal.getSubject(), principal.getTokenID(), principal.getIssuer()
                );
                return extToken1;
            }
        } catch (Throwable ignored) {
        }
        var jti = UUID.randomUUID();
        var extToken2 = Jwt.issuer(mpJwtVerifyIssuer)
                .subject(GUEST_SUBJECT_UUID)
                .upn(CustomIdentityAugmentor.GUEST_UPN)
                .groups(Set.of(CustomIdentityAugmentor.GUEST))
                .claim(Claims.jti, jti)
                .signWithSecret(secret);
        LOG.debugf("subject=%s, JTI=%s, issuer=%s", GUEST_SUBJECT_UUID, jti, mpJwtVerifyIssuer);
        return extToken2;
    }
}
