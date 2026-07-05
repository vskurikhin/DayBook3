/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * TextRecordRepository.java
 * $Id$
 */

package su.svn.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.svn.core.domain.entities.TextRecord;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link TextRecord} entities.
 * <p>
 * Provides CRUD operations and lookup methods
 * for enabled text records.
 * </p>
 */
@Repository
public interface TextRecordRepository extends JpaRepository<TextRecord, UUID> {

    /**
     * Finds an enabled text record by identifier.
     *
     * @param id record identifier
     * @return optional enabled record
     */
    Optional<TextRecord> findByIdAndEnabledTrue(UUID id);
}
