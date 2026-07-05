/*
 * This file was last modified at 2026.07.01 22:56 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordViewService.java
 * $Id$
 */

package su.svn.core.services.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import su.svn.lib.models.dto.ResourceRecordView;
import su.svn.core.models.dto.ResourceRecordViewFilter;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for retrieving filtered record views.
 *
 * <p>Supports pagination and dynamic filtering.</p>
 */
public interface RecordViewService {
    Optional<ResourceRecordView> getRecord(UUID id);
    Page<ResourceRecordView> getFilteredRecords(ResourceRecordViewFilter filter, Pageable pageable);
}
