/*
 * This file was last modified at 2026.07.01 22:56 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordViewRepository.java
 * $Id$
 */

package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.slf4j.MDC;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.Page;
import su.svn.api.repository.client.rest.RecordViewClient;
import su.svn.api.services.mappers.EntityModelResourceRecordMapper;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static su.svn.lib.Constants.REQUEST_ID;

@ApplicationScoped
public class RecordViewRepository {

    private static final Logger LOG = Logger.getLogger(RecordViewRepository.class);

    public static String SORT_LIST_PARAMS = "lastChangedTime%2Cid%2Cdesc";
    public static String SORT_PAGE_PARAMS = "postAt%2CrefreshAt%2Cid%2Cdesc";

    @Inject
    EntityModelResourceRecordMapper resourceRecordMapper;

    @Inject
    @RestClient
    RecordViewClient viewClient;

    @Inject
    SecurityContextPrincipalHelper principalHelper;

    public Uni<PostRecord> readRecord(UUID id) {
        var authorization = principalHelper.authorization();
        var requestId = MDC.get(REQUEST_ID);
        return viewClient.getRecord(authorization, requestId, id).map(resourceRecordMapper::toEntity);
    }

    public Uni<Page<PostRecord>> readPage(int pageIndex, byte size) {
        var authorization = principalHelper.authorization();
        var requestId = MDC.get(REQUEST_ID);
        return viewClient.getByPageIndexAndSizeAsUni(authorization, requestId, pageIndex, size, SORT_PAGE_PARAMS)
                .map(pageRecordView -> {
                    var list = pageRecordView.embedded().resourceRecordViewList()
                            .stream()
                            .map(resourceRecordMapper::toEntity)
                            .toList();
                    //noinspection Convert2Diamond
                    return new Page<PostRecord>(
                            list,
                            pageRecordView.page().totalPages(),
                            pageRecordView.page().number(),
                            pageRecordView.page().size(),
                            pageRecordView.page().totalElements()
                    );
                }).invoke(postRecordPage -> LOG.debugf("readPage(%d, %d): %s", pageIndex, size, postRecordPage.toString()));
    }

    public Uni<List<PostRecord>> readList(int pageIndex, int size, LocalDateTime fromTime) {
        var authorization = principalHelper.authorization();
        var requestId = MDC.get(REQUEST_ID);
        return viewClient.getByPageIndexAndSizeAndFromTimeAsUni(
                authorization, requestId, pageIndex, size, SORT_LIST_PARAMS, fromTime, true
        ).map(pageRecordView -> pageRecordView.embedded().resourceRecordViewList()
                .stream()
                .map(resourceRecordMapper::toEntity)
                .toList());
    }
}
