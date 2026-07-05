/*
 * This file was last modified at 2026.05.21 23:42 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * SetRecordRepository.java
 * $Id$
 */

package su.svn.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import su.svn.core.domain.entities.SetRecord;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link SetRecord} entities.
 *
 * <p>Provides CRUD operations and specification-based querying support.</p>
 */
@Repository
public interface SetRecordRepository extends JpaRepository<SetRecord, UUID>, JpaSpecificationExecutor<SetRecord> {
    Optional<SetRecord> findByIdAndEnabledTrue(UUID id);
}
