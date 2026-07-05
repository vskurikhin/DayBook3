/*
 * This file was last modified at 2026.05.21 23:42 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BlobRecordRepository.java
 * $Id$
 */

package su.svn.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.svn.core.domain.entities.BlobRecord;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlobRecordRepository extends JpaRepository<BlobRecord, UUID> {
    Optional<BlobRecord> findByIdAndEnabledTrue(UUID id);
}
