/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ValueRecordController.java
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
import su.svn.core.models.dto.NewValueRecord;
import su.svn.core.models.dto.ResourceValueRecord;
import su.svn.core.models.dto.UpdateValueRecord;
import su.svn.core.services.domain.ValueRecordService;

import java.util.UUID;

/**
 * REST controller for value record management.
 * <p>
 * Provides endpoints for:
 * <ul>
 *     <li>creating value records</li>
 *     <li>retrieving value records</li>
 *     <li>updating value records</li>
 *     <li>logical deletion of value records</li>
 * </ul>
 * </p>
 *
 * <p>
 * Modification operations require authenticated users
 * with the {@code USER} role.
 * </p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/core/api/v2/value-record")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValueRecordController {

    /**
     * Service responsible for value record business operations.
     */
    ValueRecordService recordService;

    /**
     * Creates a new value record.
     *
     * @param record DTO containing value record creation data
     * @return {@link ResponseEntity} containing the created
     * {@link ResourceValueRecord} and HTTP status
     * {@code 201 Created}
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceValueRecord> createValueRecord(@RequestBody @Valid NewValueRecord record) {
        ResourceValueRecord createdRecord = recordService.save(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
    }

    /**
     * Retrieves an active value record by its identifier.
     *
     * @param id the unique identifier of the value record
     * @return {@link ResponseEntity} containing the requested
     * {@link ResourceValueRecord} and HTTP status
     * {@code 200 OK}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceValueRecord> readValueRecord(@PathVariable UUID id) {
        ResourceValueRecord record = recordService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(record);
    }

    /**
     * Updates an existing value record.
     *
     * @param record DTO containing updated value record data
     * @return {@link ResponseEntity} containing the updated
     * {@link ResourceValueRecord} and HTTP status
     * {@code 200 OK}
     */
    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceValueRecord> updateValueRecord(@RequestBody @Valid UpdateValueRecord record) {
        ResourceValueRecord updatedRecord = recordService.update(record);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * Performs logical deletion of a value record.
     * <p>
     * The record remains stored in the database but is marked
     * as disabled and excluded from active queries.
     * </p>
     *
     * @param id the unique identifier of the value record
     * @return {@link ResponseEntity} with HTTP status
     * {@code 204 No Content}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteValueRecord(@PathVariable UUID id) {
        recordService.disable(id);
        return ResponseEntity.noContent().build();
    }
}
