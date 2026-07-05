/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BlobRecordController.java
 * $Id$
 */

package su.svn.core.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import su.svn.core.models.dto.NewBlobRecord;
import su.svn.core.models.dto.ResourceBlobRecord;
import su.svn.core.models.dto.UpdateBlobRecord;
import su.svn.core.services.domain.BlobRecordService;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/core/api/v2/blob-record")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlobRecordController {

    /**
     * Service responsible for Binary Large Object record business logic.
     */
    BlobRecordService recordService;

    /**
     * Creates a new Binary Large Object record.
     *
     * @param record the input data for creating a new record
     * @return {@link ResponseEntity} containing the created {@link ResourceBlobRecord}
     * and HTTP status {@code 201 Created}
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceBlobRecord> createBlobRecord(@RequestBody @Valid NewBlobRecord record) {
        ResourceBlobRecord createdRecord = recordService.save(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
    }

    /**
     * Retrieves a Binary Large Object record by its unique identifier.
     *
     * @param id the UUID of the record
     * @return {@link ResponseEntity} containing the found {@link ResourceBlobRecord}
     * and HTTP status {@code 200 OK}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceBlobRecord> readBlobRecord(@PathVariable UUID id) {
        ResourceBlobRecord record = recordService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(record);
    }

    /**
     * Updates an existing Binary Large Object record.
     *
     * @param record the updated record data
     * @return {@link ResponseEntity} containing the updated {@link ResourceBlobRecord}
     * and HTTP status {@code 200 OK}
     */
    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceBlobRecord> updateBlobRecord(@RequestBody @Valid UpdateBlobRecord record) {
        ResourceBlobRecord updatedRecord = recordService.update(record);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * Disables (soft deletes) a Binary Large Object record by its identifier.
     *
     * <p>The record is not physically removed from the database but marked as disabled.</p>
     *
     * @param id the UUID of the record to disable
     * @return {@link ResponseEntity} with HTTP status {@code 204 No Content}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteBlobRecord(@PathVariable UUID id) {
        recordService.disable(id);
        return ResponseEntity.noContent().build();
    }
}
