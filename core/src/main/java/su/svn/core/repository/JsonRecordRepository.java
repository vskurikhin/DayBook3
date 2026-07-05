/*
 * This file was last modified at 2026.03.27 14:01 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecordRepository.java
 * $Id$
 */

package su.svn.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import su.svn.core.domain.entities.JsonRecord;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link JsonRecord} entities.
 *
 * <p>Provides CRUD operations and specification-based querying.</p>
 */
@Repository
public interface JsonRecordRepository extends JpaRepository<JsonRecord, UUID>, JpaSpecificationExecutor<JsonRecord> {
    Optional<JsonRecord> findByIdAndEnabledTrue(UUID id);
}
