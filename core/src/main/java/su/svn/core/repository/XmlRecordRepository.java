/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * XmlRecordRepository.java
 * $Id$
 */

package su.svn.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.svn.core.domain.entities.XmlRecord;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link XmlRecord} persistence operations.
 *
 * <p>
 * Provides methods for accessing active XML records.
 * </p>
 */
@Repository
public interface XmlRecordRepository extends JpaRepository<XmlRecord, UUID> {

    /**
     * Finds an enabled XML record by identifier.
     *
     * @param id unique record identifier
     * @return optional enabled XML record
     */
    Optional<XmlRecord> findByIdAndEnabledTrue(UUID id);
}
