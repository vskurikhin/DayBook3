/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecordRepository.java
 * $Id$
 */

package su.svn.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.svn.core.domain.entities.VectorRecord;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link VectorRecord} persistence operations.
 *
 * <p>
 * Extends Spring Data {@link org.springframework.data.jpa.repository.JpaRepository}
 * and provides methods for accessing vector-based records.
 * </p>
 */
@Repository
public interface VectorRecordRepository extends JpaRepository<VectorRecord, UUID> {

    /**
     * Finds an enabled vector record by identifier.
     *
     * @param id unique identifier of the vector record
     * @return optional containing the found record or empty if not found
     */
    Optional<VectorRecord> findByIdAndEnabledTrue(UUID id);
}
