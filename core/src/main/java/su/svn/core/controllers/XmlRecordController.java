/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * XmlRecordController.java
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
import su.svn.core.models.dto.NewXmlRecord;
import su.svn.core.models.dto.ResourceXmlRecord;
import su.svn.core.models.dto.UpdateXmlRecord;
import su.svn.core.services.domain.XmlRecordService;

import java.util.UUID;

/**
 * REST controller for XML record management.
 * <p>
 * Provides endpoints for:
 * <ul>
 *     <li>creating XML records</li>
 *     <li>reading XML records</li>
 *     <li>updating XML records</li>
 *     <li>logical deletion of XML records</li>
 * </ul>
 * </p>
 *
 * <p>
 * All modifying operations require the {@code USER} role.
 * </p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/core/api/v2/xml-record")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class XmlRecordController {

    /**
     * Service for XML record operations.
     */
    XmlRecordService recordService;

    /**
     * Creates a new XML record.
     *
     * @param record DTO containing XML record creation data
     * @return response containing the created XML record
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceXmlRecord> createXmlRecord(
            @RequestBody @Valid NewXmlRecord record
    ) {
        ResourceXmlRecord createdRecord = recordService.save(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
    }

    /**
     * Retrieves an XML record by its identifier.
     *
     * @param id unique XML record identifier
     * @return response containing the requested XML record
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceXmlRecord> readXmlRecord(@PathVariable UUID id) {
        ResourceXmlRecord record = recordService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(record);
    }

    /**
     * Updates an existing XML record.
     *
     * @param record DTO containing updated XML record data
     * @return response containing the updated XML record
     */
    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceXmlRecord> updateXmlRecord(
            @RequestBody @Valid UpdateXmlRecord record
    ) {
        ResourceXmlRecord updatedRecord = recordService.update(record);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * Disables an XML record by its identifier.
     * <p>
     * The record becomes unavailable for active queries
     * after successful deletion.
     * </p>
     *
     * @param id unique XML record identifier
     * @return empty response with HTTP 204 status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteXmlRecord(@PathVariable UUID id) {
        recordService.disable(id);
        return ResponseEntity.noContent().build();
    }
}