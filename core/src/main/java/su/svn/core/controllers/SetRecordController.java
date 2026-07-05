/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * SetRecordController.java
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
import su.svn.core.models.dto.NewSetRecord;
import su.svn.core.models.dto.ResourceSetRecord;
import su.svn.core.models.dto.UpdateSetRecord;
import su.svn.core.services.domain.SetRecordService;

import java.util.UUID;

/**
 * REST controller for managing {@code SetRecord} resources.
 *
 * <p>Provides endpoints for:
 * <ul>
 *     <li>Creating records</li>
 *     <li>Reading records</li>
 *     <li>Updating records</li>
 *     <li>Logical deletion</li>
 * </ul>
 * </p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/core/api/v2/set-record")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SetRecordController {

    SetRecordService recordService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceSetRecord> createSetRecord(@RequestBody @Valid NewSetRecord record) {
        ResourceSetRecord createdRecord = recordService.save(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceSetRecord> readSetRecord(@PathVariable UUID id) {
        ResourceSetRecord record = recordService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(record);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceSetRecord> updateSetRecord(@RequestBody @Valid UpdateSetRecord record) {
        ResourceSetRecord updatedRecord = recordService.update(record);
        return ResponseEntity.ok(updatedRecord);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteSetRecord(@PathVariable UUID id) {
        recordService.disable(id);
        return ResponseEntity.noContent().build();
    }
}
