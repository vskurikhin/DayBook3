/*
 * This file was last modified at 2026.04.05 22:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ResourceRecordViewFilter.java
 * $Id$
 */

package su.svn.core.models.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record ResourceRecordViewFilter(
        String title,
        LocalDateTime fromTime,
        OffsetDateTime fromDate,
        OffsetDateTime toDate,
        Boolean withDisabled) {
}
