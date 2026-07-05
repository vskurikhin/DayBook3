/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * XmlRecordRepository.java
 * $Id$
 */

package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.MDC;
import su.svn.api.models.dto.NewXmlRecord;
import su.svn.api.models.dto.ResourceXmlRecord;
import su.svn.api.models.dto.UpdateXmlRecord;
import su.svn.api.repository.client.rest.XmlRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.util.Objects;
import java.util.UUID;

import static su.svn.lib.Constants.REQUEST_ID;

/**
 * Repository responsible for XML record operations.
 *
 * <p>
 * This repository acts as an adapter between the application layer
 * and the remote XML REST service.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Propagate authorization information</li>
 *     <li>Attach request correlation identifiers</li>
 *     <li>Delegate XML operations to {@link XmlRecordClient}</li>
 * </ul>
 *
 * <p>
 * All operations are asynchronous and return Mutiny {@link Uni}.
 * </p>
 *
 * @see XmlRecordClient
 * @see SecurityContextPrincipalHelper
 * @see io.smallrye.mutiny.Uni
 */
@ApplicationScoped
public class XmlRecordRepository {

    public static final String NONE = "NONE";

    @Inject
    @RestClient
    XmlRecordClient client;

    @Inject
    SecurityContextPrincipalHelper principalHelper;

    /**
     * Deletes an XML record.
     *
     * @param id XML record identifier
     * @return completion notification
     */
    public Uni<Void> delete(UUID id) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.delete(authorization, requestId, id);
    }

    /**
     * Creates a new XML record.
     *
     * @param newRecord XML creation DTO
     * @return created XML resource
     */
    public Uni<ResourceXmlRecord> post(NewXmlRecord newRecord) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.post(authorization, requestId, newRecord);
    }

    /**
     * Updates an existing XML record.
     *
     * @param updateRecord XML update DTO
     * @return updated XML resource
     */
    public Uni<ResourceXmlRecord> put(UpdateXmlRecord updateRecord) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.put(authorization, requestId, updateRecord);
    }
}
