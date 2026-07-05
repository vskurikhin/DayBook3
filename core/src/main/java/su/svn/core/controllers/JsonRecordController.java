/*
 * This file was last modified at 2026.04.23 20:14 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecordController.java
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
import su.svn.core.models.dto.NewJsonRecord;
import su.svn.core.models.dto.ResourceJsonRecord;
import su.svn.core.models.dto.UpdateJsonRecord;
import su.svn.core.services.domain.JsonRecordService;

import java.util.UUID;
/**
 * REST controller for managing JSON-based records.
 *
 * <p>This controller provides CRUD operations for {@link ResourceJsonRecord}
 * resources via HTTP endpoints. It delegates all business logic to the
 * {@link JsonRecordService}.</p>
 *
 * <p>Base URL: <b>/core/api/v2/json-record</b></p>
 *
 * <h2>Endpoints</h2>
 * <ul>
 *     <li><b>POST /</b> — Create a new JSON record</li>
 *     <li><b>GET /{id}</b> — Retrieve a JSON record by its identifier</li>
 *     <li><b>PUT /</b> — Update an existing JSON record</li>
 *     <li><b>DELETE /{id}</b> — Disable (soft delete) a JSON record</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <p>Exceptions thrown by this controller are handled globally by
 * {@code GlobalExceptionHandler}, which converts them into appropriate
 * HTTP responses.</p>
 *
 * <h2>Validation</h2>
 * <p>Incoming request bodies are validated using {@link jakarta.validation.Valid}.
 * If validation fails, a {@code 400 Bad Request} response is returned.</p>
 *
 * @author Victor N. Skurikhin
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/core/api/v2/json-record")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JsonRecordController {

    /**
     * Service responsible for JSON record business logic.
     */
    JsonRecordService jsonRecordService;

    /**
     * Creates a new JSON record.
     *
     * @param record the input data for creating a new record
     * @return {@link ResponseEntity} containing the created {@link ResourceJsonRecord}
     *         and HTTP status {@code 201 Created}
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceJsonRecord> createJsonRecord(@RequestBody @Valid NewJsonRecord record) {
        ResourceJsonRecord createdRecord = jsonRecordService.save(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
    }

    /**
     * Retrieves a JSON record by its unique identifier.
     *
     * @param id the UUID of the record
     * @return {@link ResponseEntity} containing the found {@link ResourceJsonRecord}
     *         and HTTP status {@code 200 OK}
     * @throws ChangeSetPersister.NotFoundException if the record is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceJsonRecord> readJsonRecord(@PathVariable UUID id) throws ChangeSetPersister.NotFoundException {
        ResourceJsonRecord record = jsonRecordService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(record);
    }

    /**
     * Updates an existing JSON record.
     *
     * @param record the updated record data
     * @return {@link ResponseEntity} containing the updated {@link ResourceJsonRecord}
     *         and HTTP status {@code 200 OK}
     */
    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResourceJsonRecord> updateJsonRecord(@RequestBody @Valid UpdateJsonRecord record) {
        ResourceJsonRecord updatedRecord = jsonRecordService.update(record);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * Disables (soft deletes) a JSON record by its identifier.
     *
     * <p>The record is not physically removed from the database but marked as disabled.</p>
     *
     * @param id the UUID of the record to disable
     * @return {@link ResponseEntity} with HTTP status {@code 204 No Content}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteJsonRecord(@PathVariable UUID id) {
        jsonRecordService.disable(id);
        return ResponseEntity.noContent().build();
    }
}
