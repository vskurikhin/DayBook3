/*
 * This file was last modified at 2026.05.07 14:57 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * CustomIdentityAugmentor.java
 * $Id$
 */

package su.svn.api.services.security;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import su.svn.api.repository.client.rest.AuthRolesClient;

import java.util.Set;

/**
 * {@link SecurityIdentityAugmentor} implementation that enriches the current
 * {@link SecurityIdentity} with roles and user attributes fetched from an external REST service.
 *
 * <p>This augmentor performs the following steps:
 * <ul>
 *     <li>Extracts the JWT token from {@link io.quarkus.security.credential.TokenCredential}</li>
 *     <li>Invokes external {@link AuthRolesClient} to retrieve user roles and username</li>
 *     <li>Adds retrieved roles to the {@link SecurityIdentity}</li>
 *     <li>Adds {@code upn} (user principal name) as an attribute</li>
 *     <li>Returns a new enriched {@link SecurityIdentity}</li>
 * </ul>
 *
 * <p>This class is executed automatically by Quarkus during authentication phase.
 */
@ApplicationScoped
public class CustomIdentityAugmentor implements SecurityIdentityAugmentor {

    private static final Logger LOG = Logger.getLogger(CustomIdentityAugmentor.class);

    static final String BEARER = "Bearer ";

    /**
     * Attribute key used to store user principal name (UPN) in {@link SecurityIdentity}.
     */
    static final String UPN = "upn";
    static final String GUEST = "GUEST";
    static final String GUEST_UPN = "guest";

    @Inject
    @RestClient
    AuthRolesClient client;

    /**
     * Augments the given {@link SecurityIdentity} by fetching additional roles and user data
     * from an external service.
     *
     * @param identity the current security identity
     * @param context  the authentication request context
     * @return a {@link Uni} producing the augmented {@link SecurityIdentity}
     */
    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {

        var tokenCredential = identity.getCredential(io.quarkus.security.credential.TokenCredential.class);
        if (tokenCredential == null) {
            var securityIdentity = QuarkusSecurityIdentity.builder(identity)
                    .addRoles(Set.of(GUEST))
                    .addAttribute(UPN, GUEST_UPN)
                    .build();
            LOG.debugf("securityIdentity with %s role", GUEST); // TODO remove
            return Uni.createFrom().item(securityIdentity);
        }
        var token = tokenCredential.getToken();
        LOG.debugf("token: %s", token); // TODO remove

        return client.getUserHasRoles(BEARER + token)
                .onItem().transform(user -> {
                    var builder = QuarkusSecurityIdentity.builder(identity)
                            .addRoles(user.data().roles())
                            .addAttribute(UPN, user.data().userName())
                            .setPrincipal(identity.getPrincipal());
                    return builder.build();
                });
    }
}
