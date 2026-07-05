/*
 * This file was last modified at 2026.05.21 16:48 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * CustomJWTCallerPrincipalFactory.java
 * $Id$
 */

package su.svn.api.services.security;

import io.smallrye.jwt.algorithm.SignatureAlgorithm;
import io.smallrye.jwt.auth.principal.*;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import su.svn.api.models.exceptions.CustomParseException;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Set;

/**
 * Custom implementation of {@link JWTCallerPrincipalFactory} used to parse and validate
 * HMAC-signed JWT tokens (HS256) using a shared secret.
 *
 * <p>This factory overrides the default Quarkus JWT parsing mechanism and enforces:
 * <ul>
 *     <li>Signature algorithm: HS256</li>
 *     <li>Verification using a configured HMAC secret</li>
 *     <li>No expiration time limit (TTL disabled)</li>
 * </ul>
 *
 * <p>The secret key is injected from the configuration property:
 * <pre>
 * application.external-jwt-verify-key-hmac
 * </pre>
 *
 * <p>On successful parsing, a {@link DefaultJWTCallerPrincipal} is created using:
 * <ul>
 *     <li>The "typ" header value as the principal name</li>
 *     <li>The parsed JWT claims</li>
 * </ul>
 *
 * <p>If parsing or verification fails, a {@link CustomParseException} is thrown.
 *
 * <p><b>Important:</b>
 * <ul>
 *     <li>This implementation assumes symmetric key (HMAC) JWTs</li>
 *     <li>It bypasses the default {@link JWTAuthContextInfo} provided by Quarkus</li>
 *     <li>It should not perform blocking or external I/O operations</li>
 * </ul>
 *
 * @see JWTCallerPrincipalFactory
 * @see DefaultJWTCallerPrincipal
 * @see JWTAuthContextInfo
 */
@ApplicationScoped
@Alternative
@Priority(1)
public class CustomJWTCallerPrincipalFactory extends JWTCallerPrincipalFactory {

    private static final Logger LOG = Logger.getLogger(CustomJWTCallerPrincipalFactory.class);

    @ConfigProperty(name = "app.external-jwt-verify-key-hmac")
    String secret;

    private final DefaultJWTTokenParser defaultParser = new DefaultJWTTokenParser();

    @Override
    public JWTCallerPrincipal parse(String token, JWTAuthContextInfo authContextInfo) {

        var contextInfo = new JWTAuthContextInfo();
        contextInfo.setSignatureAlgorithm(Set.of(SignatureAlgorithm.HS256));
        var key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        contextInfo.setSecretVerificationKey(key);
        contextInfo.setMaxTimeToLiveSecs((long) -1);

        try {
            var jwtContext = defaultParser.parse(token, contextInfo);
            var type = jwtContext.getJoseObjects().getFirst().getHeader("typ");
            var claims = jwtContext.getJwtClaims();
            LOG.debugf("claims: %s", claims.toString()); // TODO remove
            return new DefaultJWTCallerPrincipal(type, claims);
        } catch (ParseException e) {
            LOG.errorf("%s stack trace %s", e.getMessage(), Arrays.stream(e.getStackTrace()).limit(10).toList());
            throw new CustomParseException(e);
        }
    }
}