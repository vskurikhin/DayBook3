/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * MarkdownRecordRepository.java
 * $Id$
 */

package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.MDC;
import su.svn.api.models.dto.NewMarkdownRecord;
import su.svn.api.models.dto.ResourceMarkdownRecord;
import su.svn.api.models.dto.UpdateMarkdownRecord;
import su.svn.api.repository.client.rest.MarkdownRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.util.Objects;
import java.util.UUID;

import static su.svn.lib.Constants.REQUEST_ID;

/**
 * Repository responsible for interaction with the remote
 * markdown record REST service.
 *
 * <p>
 * Adds authorization and request tracing metadata
 * before delegating requests to the REST client.
 * </p>
 */
@ApplicationScoped
public class MarkdownRecordRepository {

    public static final String NONE = "NONE";

    @Inject
    @RestClient
    MarkdownRecordClient client;

    @Inject
    SecurityContextPrincipalHelper principalHelper;

    /**
     * Deletes a markdown record.
     *
     * @param id identifier of the record to delete
     * @return asynchronous completion signal
     */
    public Uni<Void> delete(UUID id) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.delete(authorization, requestId, id);
    }

    /**
     * Creates a new markdown record.
     *
     * @param newRecord DTO containing new record data
     * @return asynchronously created markdown record resource
     */
    public Uni<ResourceMarkdownRecord> post(NewMarkdownRecord newRecord) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.post(authorization, requestId, newRecord);
    }

    /**
     * Updates an existing markdown record.
     *
     * @param updateRecord DTO containing updated record data
     * @return asynchronously updated markdown record resource
     */
    public Uni<ResourceMarkdownRecord> put(UpdateMarkdownRecord updateRecord) {
        var authorization = principalHelper.authorization();
        var requestId = Objects.toString(MDC.get(REQUEST_ID), NONE);
        return client.put(authorization, requestId, updateRecord);
    }
}
