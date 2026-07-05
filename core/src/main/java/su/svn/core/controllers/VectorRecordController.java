/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecordController.java
 * $Id$
 */

package su.svn.core.controllers;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import su.svn.core.models.dto.NewVectorRecord;
import su.svn.core.models.dto.ResourceVectorRecord;
import su.svn.core.models.dto.UpdateVectorRecord;
import su.svn.core.services.domain.VectorRecordService;

import java.util.UUID;

/**
 * REST controller for vector record management.
 * <p>
 * Provides endpoints for:
 * <ul>
 *     <li>creating vector records</li>
 *     <li>retrieving vector records</li>
 *     <li>updating vector records</li>
 *     <li>logical deletion of vector records</li>
 * </ul>
 * </p>
 *
 * <p>
 * Vector records contain vector embeddings and related metadata
 * used for semantic search and machine learning operations.
 * </p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/core/api/v2/vector-record")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VectorRecordController {

    /**
     * Service responsible for vector record business logic.
     */
    VectorRecordService recordService;

    /**
     * Creates a new vector record.
     *
     * @param record DTO containing data for the new vector record
     * @return {@link ResponseEntity} containing the created
     * {@link ResourceVectorRecord} and HTTP status {@code 201 Created}
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceVectorRecord> createVectorRecord(@RequestBody @Valid NewVectorRecord record) {
        ResourceVectorRecord createdRecord = recordService.save(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
    }

    /**
     * Retrieves a vector record by its unique identifier.
     *
     * @param id the UUID of the vector record
     * @return {@link ResponseEntity} containing the found
     * {@link ResourceVectorRecord} and HTTP status {@code 200 OK}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceVectorRecord> readVectorRecord(@PathVariable UUID id) {
        ResourceVectorRecord record = recordService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(record);
    }

    /**
     * Updates an existing vector record.
     *
     * @param record DTO containing updated vector record data
     * @return {@link ResponseEntity} containing the updated
     * {@link ResourceVectorRecord} and HTTP status {@code 200 OK}
     */
    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceVectorRecord> updateVectorRecord(@RequestBody @Valid UpdateVectorRecord record) {
        ResourceVectorRecord updatedRecord = recordService.update(record);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * Disables a vector record by its identifier.
     * <p>
     * The record is logically deleted and becomes unavailable
     * for active queries.
     * </p>
     *
     * @param id the UUID of the vector record to disable
     * @return {@link ResponseEntity} with HTTP status {@code 204 No Content}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteVectorRecord(@PathVariable UUID id) {
        recordService.disable(id);
        return ResponseEntity.noContent().build();
    }
}
