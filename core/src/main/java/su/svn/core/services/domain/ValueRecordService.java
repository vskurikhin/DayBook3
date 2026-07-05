/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ValueRecordService.java
 * $Id$
 */

package su.svn.core.services.domain;

import su.svn.core.models.dto.NewValueRecord;
import su.svn.core.models.dto.ResourceValueRecord;
import su.svn.core.models.dto.UpdateValueRecord;

import java.util.UUID;

public interface ValueRecordService {

    void disable(UUID id);

    ResourceValueRecord findById(UUID id);

    ResourceValueRecord save(NewValueRecord newRecord);

    ResourceValueRecord update(UpdateValueRecord updateRecord);
}