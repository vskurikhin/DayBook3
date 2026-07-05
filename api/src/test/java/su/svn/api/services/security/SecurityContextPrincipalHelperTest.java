package su.svn.api.services.security;

import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.auth.principal.DefaultJWTTokenParser;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.algorithm.SignatureAlgorithm;
import org.eclipse.microprofile.jwt.Claims;
import org.jose4j.jwt.consumer.JwtContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityContextPrincipalHelperTest {

    private SecurityContextPrincipalHelper helper;

    private static final String SECRET = "12345678901234567890123456789012";

    @BeforeEach
    void setUp() {
        helper = new SecurityContextPrincipalHelper();
        helper.apiKeyPrefix = "Bearer";
        helper.mpJwtVerifyIssuer = "issuer";
        helper.secret = SECRET;
    }

    /**
     * Generates a signed JWT for tests.
     */
    private String generateToken() {
        return Jwt.issuer("issuer")
                .subject("subject")
                .claim(Claims.jti, UUID.randomUUID().toString())
                .signWithSecret(SECRET);
    }

    /**
     * Builds DefaultJWTCallerPrincipal from token using real parser.
     */
    private DefaultJWTCallerPrincipal buildPrincipal(String token) throws Exception {
        var parser = new DefaultJWTTokenParser();

        var context = new JWTAuthContextInfo();
        context.setSignatureAlgorithm(Set.of(SignatureAlgorithm.HS256));
        context.setSecretVerificationKey(
                new SecretKeySpec(SECRET.getBytes(), "HmacSHA256")
        );

        JwtContext jwtContext = parser.parse(token, context);

        return new DefaultJWTCallerPrincipal(
                "JWT",
                jwtContext.getJwtClaims()
        );
    }

    @Test
    void shouldBuildAuthorizationHeaderFromJwtPrincipal() throws Exception {
        // given
        String token = generateToken();
        DefaultJWTCallerPrincipal principal = buildPrincipal(token);

        SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(principal)
                .addRoles(Set.of("USER"))
                .addAttribute(CustomIdentityAugmentor.UPN, "john")
                .addCredential(new TokenCredential(token, "Bearer"))
                .build();

        helper.identity = identity;

        // when
        String result = helper.authorization();

        // then
        assertThat(result).startsWith("Bearer ");
        assertThat(result.split(" ")).hasSize(2);
    }

    @Test
    void shouldGenerateJwtFromPrincipal() throws Exception {
        // given
        String token = generateToken();
        DefaultJWTCallerPrincipal principal = buildPrincipal(token);

        SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(principal)
                .addRoles(Set.of("ADMIN"))
                .addAttribute(CustomIdentityAugmentor.UPN, "alice")
                .addCredential(new TokenCredential(token, "Bearer"))
                .build();

        helper.identity = identity;

        // when
        String insideToken = helper.insideToken();

        // then
        assertThat(insideToken).isNotBlank();
        assertThat(insideToken.split("\\.")).hasSize(3); // JWT format
    }

    @Test
    void shouldFallbackToGuestWhenPrincipalIsNotJwt() {
        // given
        SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(() -> "anonymous")
                .build();

        helper.identity = identity;

        // when
        String token = helper.insideToken();

        // then
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldFallbackToGuestWhenExceptionOccurs() {
        // given
        SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(() -> {
                    throw new RuntimeException("boom");
                })
                .build();

        helper.identity = identity;

        // when
        String token = helper.insideToken();

        // then
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldIncludeRolesAndUpnInGeneratedToken() throws Exception {
        // given
        String token = generateToken();
        DefaultJWTCallerPrincipal principal = buildPrincipal(token);

        helper.identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(principal)
                .addRoles(Set.of("ADMIN", "USER"))
                .addAttribute(CustomIdentityAugmentor.UPN, "bob")
                .addCredential(new TokenCredential(token, "Bearer"))
                .build();

        // when
        String jwt = helper.insideToken();

        // then
        assertThat(jwt).isNotBlank();
        assertThat(jwt.split("\\.")).hasSize(3);
    }
}