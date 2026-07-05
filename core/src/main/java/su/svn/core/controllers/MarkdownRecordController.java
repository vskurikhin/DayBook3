/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * MarkdownRecordController.java
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
import su.svn.core.models.dto.NewMarkdownRecord;
import su.svn.core.models.dto.ResourceMarkdownRecord;
import su.svn.core.models.dto.UpdateMarkdownRecord;
import su.svn.core.services.domain.MarkdownRecordService;

import java.util.UUID;

/**
 * REST controller for markdown record management.
 * <p>
 * Provides endpoints for:
 * <ul>
 *     <li>creating markdown records</li>
 *     <li>retrieving markdown records</li>
 *     <li>updating markdown records</li>
 *     <li>logical deletion of markdown records</li>
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
@RequestMapping("/core/api/v2/markdown-record")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkdownRecordController {

    /**
     * Service responsible for markdown record business operations.
     */
    MarkdownRecordService recordService;

    /**
     * Creates a new markdown record.
     *
     * @param record DTO containing markdown record creation data
     * @return {@link ResponseEntity} containing the created
     * {@link ResourceMarkdownRecord} and HTTP status
     * {@code 201 Created}
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceMarkdownRecord> createMarkdownRecord(@RequestBody @Valid NewMarkdownRecord record) {
        ResourceMarkdownRecord createdRecord = recordService.save(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
    }

    /**
     * Retrieves an active markdown record by its identifier.
     *
     * @param id the unique identifier of the markdown record
     * @return {@link ResponseEntity} containing the requested
     * {@link ResourceMarkdownRecord} and HTTP status
     * {@code 200 OK}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceMarkdownRecord> readMarkdownRecord(@PathVariable UUID id) {
        ResourceMarkdownRecord record = recordService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(record);
    }

    /**
     * Updates an existing markdown record.
     *
     * @param record DTO containing updated markdown record data
     * @return {@link ResponseEntity} containing the updated
     * {@link ResourceMarkdownRecord} and HTTP status
     * {@code 200 OK}
     */
    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceMarkdownRecord> updateMarkdownRecord(@RequestBody @Valid UpdateMarkdownRecord record) {
        ResourceMarkdownRecord updatedRecord = recordService.update(record);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * Performs logical deletion of a markdown record.
     * <p>
     * The record remains stored in the database but is marked
     * as disabled and excluded from active queries.
     * </p>
     *
     * @param id the unique identifier of the markdown record
     * @return {@link ResponseEntity} with HTTP status
     * {@code 204 No Content}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMarkdownRecord(@PathVariable UUID id) {
        recordService.disable(id);
        return ResponseEntity.noContent().build();
    }
}
