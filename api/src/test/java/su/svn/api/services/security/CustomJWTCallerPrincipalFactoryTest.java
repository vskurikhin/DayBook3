package su.svn.api.services.security;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import su.svn.api.models.exceptions.CustomParseException;
import su.svn.api.profile.NoContainersProfile;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestProfile(NoContainersProfile.class)
class CustomJWTCallerPrincipalFactoryTest {

    @Inject
    CustomJWTCallerPrincipalFactory factory;

    private static final String SECRET = "12345678901234567890123456789012";

    /**
     * Helper method to generate a valid HS256 token.
     */
    private String generateToken() {
        return Jwt.issuer("test-issuer")
                .subject(UUID.randomUUID().toString())
                .claim("custom", "value")
                .signWithSecret(SECRET);
    }

    @Test
    void shouldParseValidToken() {
        // given
        String token = generateToken();

        // when
        var principal = factory.parse(token, null);

        // then
        assertThat(principal).isNotNull();
    }

    @Test
    void shouldContainExpectedClaims() {
        // given
        String subject = UUID.randomUUID().toString();

        String token = Jwt.issuer("test-issuer")
                .subject(subject)
                .signWithSecret(SECRET);

        // when
        var principal = factory.parse(token, null);

        // then
        assertThat(principal.getSubject()).isEqualTo(subject);
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        // given
        String invalidToken = "invalid.token.value";

        // when / then
        assertThatThrownBy(() -> factory.parse(invalidToken, null))
                .isInstanceOf(CustomParseException.class);
    }

    @Test
    void shouldThrowExceptionForWrongSignature() {
        // given
        String token = Jwt.issuer("test-issuer")
                .subject("user")
                .signWithSecret("wrong-secret-12345678901234567890");

        // when / then
        assertThatThrownBy(() -> factory.parse(token, null))
                .isInstanceOf(CustomParseException.class);
    }
}
