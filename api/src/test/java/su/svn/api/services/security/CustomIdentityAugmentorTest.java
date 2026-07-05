package su.svn.api.services.security;

import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import su.svn.api.models.dto.SessionRolesData;
import su.svn.api.models.dto.UserHasRoles;
import su.svn.api.repository.client.rest.AuthRolesClient;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomIdentityAugmentorTest {

    private CustomIdentityAugmentor augmentor;
    private AuthRolesClient client;

    @BeforeEach
    void setUp() {
        augmentor = new CustomIdentityAugmentor();
        client = mock(AuthRolesClient.class);
        augmentor.client = client;
    }

    private SecurityIdentity buildIdentity(String token) {
        return QuarkusSecurityIdentity.builder()
                .setPrincipal((Principal) () -> "test-user")
                .addCredential(new TokenCredential(token, "Bearer"))
                .build();
    }

    private SessionRolesData mockResponse(String username, Set<String> roles) {
        return new SessionRolesData(true, new UserHasRoles(username, roles, null, null));
    }

    @Test
    void shouldAugmentIdentityWithRolesAndUpn() {
        // given
        String token = "test-token";

        SecurityIdentity identity = buildIdentity(token);

        var response = mockResponse("john", Set.of("USER", "ADMIN"));

        when(client.getUserHasRoles("Bearer " + token))
                .thenReturn(Uni.createFrom().item(response));

        // when
        SecurityIdentity result = augmentor
                .augment(identity, mock(AuthenticationRequestContext.class))
                .await().indefinitely();

        // then
        assertThat(result.getRoles()).containsExactlyInAnyOrder("USER", "ADMIN");
        assertThat(Optional.ofNullable(result.getAttribute(CustomIdentityAugmentor.UPN))).isEqualTo(Optional.of("john"));
        assertThat(result.getPrincipal().getName()).isEqualTo("test-user");
    }

    @Test
    void shouldPreserveExistingRoles() {
        // given
        String token = "test-token";

        SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal((Principal) () -> "user")
                .addRoles(Set.of("EXISTING"))
                .addCredential(new TokenCredential(token, "Bearer"))
                .build();

        var response = mockResponse("john", Set.of("NEW_ROLE"));

        when(client.getUserHasRoles("Bearer " + token))
                .thenReturn(Uni.createFrom().item(response));

        // when
        SecurityIdentity result = augmentor
                .augment(identity, mock(AuthenticationRequestContext.class))
                .await().indefinitely();

        // then
        assertThat(result.getRoles()).contains("EXISTING", "NEW_ROLE");
    }

    @Test
    void shouldCallExternalServiceWithBearerToken() {
        // given
        String token = "abc123";
        SecurityIdentity identity = buildIdentity(token);

        when(client.getUserHasRoles(any()))
                .thenReturn(Uni.createFrom().item(mockResponse("user", Set.of())));

        // when
        augmentor.augment(identity, mock(AuthenticationRequestContext.class))
                .await().indefinitely();

        // then
        verify(client).getUserHasRoles("Bearer " + token);
    }

    @Test
    void shouldFailWhenNoTokenCredential() {
        // given
        SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal((Principal) () -> "user")
                .build();

        // when + then
        try {
            augmentor.augment(identity, mock(AuthenticationRequestContext.class))
                    .await().indefinitely();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }
}