/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecordService.java
 * $Id$
 */

package su.svn.core.services.domain;

import su.svn.core.domain.entities.JsonRecord;
import su.svn.core.models.dto.NewJsonRecord;
import su.svn.core.models.dto.ResourceJsonRecord;
import su.svn.core.models.dto.UpdateJsonRecord;

import java.util.UUID;

/**
 * Service interface for managing {@link JsonRecord}.
 *
 * <p>Defines operations for creating, updating, retrieving,
 * and disabling records.</p>
 */
public interface JsonRecordService {

    void disable(UUID id);

    ResourceJsonRecord findById(UUID id);

    ResourceJsonRecord save(NewJsonRecord newRecord);

    ResourceJsonRecord update(UpdateJsonRecord updateRecord);
}
