package su.svn.core.services.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceImplTest {

    private static final String SECRET =
            "c3VwZXItc2VjcmV0LWtleS1mb3ItdGVzdHMtc3VwZXItc2VjcmV0LWtleQ==";

    private JwtServiceImpl jwtService;

    private String validToken;

    private String expiredToken;

    @BeforeEach
    void setUp() throws Exception {

        jwtService = new JwtServiceImpl();

        Field field =
                JwtServiceImpl.class.getDeclaredField("jwtSigningKey");

        field.setAccessible(true);
        field.set(jwtService, SECRET);

        Key key = Keys.hmacShaKeyFor(
                io.jsonwebtoken.io.Decoders.BASE64.decode(SECRET)
        );

        validToken =
                Jwts.builder()
                        .claim(JwtServiceImpl.UPN, "root")
                        .claim(JwtServiceImpl.GROUPS,
                                List.of("ADMIN", "USER"))
                        .setIssuedAt(new Date())
                        .setExpiration(
                                new Date(System.currentTimeMillis() + 60000)
                        )
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

        expiredToken =
                Jwts.builder()
                        .claim(JwtServiceImpl.UPN, "root")
                        .setIssuedAt(new Date(System.currentTimeMillis() - 120000))
                        .setExpiration(
                                new Date(System.currentTimeMillis() - 60000)
                        )
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Проверяет извлечение имени пользователя из JWT токена.
     */
    @Test
    void shouldExtractUserName() {

        String userName =
                jwtService.extractUserName(validToken);

        assertThat(userName)
                .isEqualTo("root");
    }

    /**
     * Проверяет извлечение групп пользователя из JWT токена.
     */
    @Test
    void shouldExtractGroups() {

        Set<String> groups =
                jwtService.extractGroups(validToken);

        assertThat(groups)
                .containsExactlyInAnyOrder(
                        "ADMIN",
                        "USER"
                );
    }

    /**
     * Проверяет валидность корректного токена.
     */
    @Test
    void shouldValidateToken() {

        boolean result =
                jwtService.isTokenValid(validToken);

        assertThat(result)
                .isTrue();
    }

    /**
     * Проверяет, что просроченный токен считается невалидным.
     */
    @Test
    void shouldReturnFalseForExpiredToken() {

        boolean result =
                jwtService.isTokenValid(expiredToken);

        assertThat(result)
                .isFalse();
    }

    /**
     * Проверяет валидность токена при наличии UPN
     * и отсутствии authentication в SecurityContext.
     */
    @Test
    void shouldValidateTokenWithUpn() {

        boolean result =
                jwtService.isTokenValid(
                        validToken,
                        "root"
                );

        assertThat(result)
                .isTrue();
    }

    /**
     * Проверяет, что токен считается невалидным,
     * если UPN пустой.
     */
    @Test
    void shouldReturnFalseWhenUpnEmpty() {

        boolean result =
                jwtService.isTokenValid(
                        validToken,
                        ""
                );

        assertThat(result)
                .isFalse();
    }

    /**
     * Проверяет, что токен считается невалидным,
     * если authentication уже присутствует.
     */
    @Test
    void shouldReturnFalseWhenAuthenticationExists() {

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "root",
                                null
                        )
                );

        boolean result =
                jwtService.isTokenValid(
                        validToken,
                        "root"
                );

        assertThat(result)
                .isFalse();
    }

    /**
     * Проверяет корректный парсинг одной группы.
     */
    @Test
    void shouldParseSingleGroup() throws Exception {

        Key key = Keys.hmacShaKeyFor(
                io.jsonwebtoken.io.Decoders.BASE64.decode(SECRET)
        );

        String token =
                Jwts.builder()
                        .claim(JwtServiceImpl.UPN, "root")
                        .claim(JwtServiceImpl.GROUPS,
                                List.of("ADMIN"))
                        .setExpiration(
                                new Date(System.currentTimeMillis() + 60000)
                        )
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

        Set<String> groups =
                jwtService.extractGroups(token);

        assertThat(groups)
                .containsExactly("ADMIN");
    }

    /**
     * Проверяет, что группы извлекаются без квадратных скобок и пробелов.
     */
    @Test
    void shouldNormalizeGroups() {

        Set<String> groups =
                jwtService.extractGroups(validToken);

        assertThat(groups)
                .doesNotContain("[ADMIN")
                .doesNotContain(" USER]");
    }
}