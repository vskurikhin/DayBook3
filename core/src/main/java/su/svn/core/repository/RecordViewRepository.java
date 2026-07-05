/*
 * This file was last modified at 2026.03.27 14:01 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecordViewRepository.java
 * $Id$
 */

package su.svn.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import su.svn.core.domain.entities.RecordView;

import java.util.UUID;

/**
 * Repository for querying {@link su.svn.core.domain.entities.RecordView}.
 *
 * <p>Supports pagination and filtering via {@code JpaSpecificationExecutor}.</p>
 */
@Repository
public interface RecordViewRepository extends JpaRepository<RecordView, UUID>, JpaSpecificationExecutor<RecordView> {
}
