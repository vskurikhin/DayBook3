/*
 * This file was last modified at 2026.07.01 23:05 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordViewController.java
 * $Id$
 */

package su.svn.core.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import su.svn.core.models.dto.ResourceRecordViewFilter;
import su.svn.core.services.domain.RecordViewService;
import su.svn.lib.models.dto.ResourceRecordView;

import java.util.UUID;

/**
 * REST controller for accessing resource record views.
 *
 * <p>This controller provides read-only REST endpoints for retrieving
 * {@link ResourceRecordView} entities. It supports retrieving a paginated list
 * of records with filtering capabilities and retrieving a single record by its
 * unique identifier.</p>
 *
 * <p>The controller delegates all business operations to {@link RecordViewService}
 * and is responsible only for handling HTTP requests, request validation,
 * pagination parameters, and response mapping.</p>
 *
 * <p>Base URL: <b>/core/api/v2/records-view</b></p>
 *
 * <h2>Available endpoints</h2>
 *
 * <ul>
 *     <li>
 *         <b>GET /</b> — Retrieves a paginated list of record views with optional filtering.
 *     </li>
 *     <li>
 *         <b>GET /{id}</b> — Retrieves a single record view by its unique identifier.
 *     </li>
 * </ul>
 *
 * <h2>Filtering</h2>
 *
 * <p>The list endpoint supports filtering through {@link ResourceRecordViewFilter}.
 * Filter parameters are provided as HTTP query parameters and may be used to narrow
 * down the result set.</p>
 *
 * <h2>Pagination and sorting</h2>
 *
 * <p>Pagination is implemented using Spring Data's {@link Pageable}.
 * Clients can control pagination using standard query parameters:</p>
 *
 * <ul>
 *     <li><b>page</b> — zero-based page index</li>
 *     <li><b>size</b> — number of records per page</li>
 *     <li><b>sort</b> — sorting properties and direction</li>
 * </ul>
 *
 * <h2>Responses</h2>
 *
 * <p>The collection endpoint returns a HAL-based paginated representation using
 * {@link PagedModel} and {@link EntityModel}.</p>
 *
 * <p>The single record endpoint returns the requested
 * {@link ResourceRecordView} wrapped in {@link ResponseEntity}.
 * If the record does not exist, an empty response is returned with HTTP status
 * {@code 404 NOT_FOUND}.</p>
 *
 * <h2>Validation</h2>
 *
 * <p>The controller is annotated with {@link Validated} to enable validation
 * support for incoming request parameters and filter objects.</p>
 *
 * @author Victor N. Skurikhin
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/core/api/v2/records-view")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordViewController {

    /**
     * Service responsible for retrieving and filtering record views.
     */
    RecordViewService recordViewService;

    PagedResourcesAssembler<ResourceRecordView> assembler;

    /**
     * Retrieves a paginated list of record views based on the provided filter and pagination parameters.
     *
     * @param filter   filter criteria for narrowing down results
     * @param pageable pagination and sorting information
     * @return {@link ResponseEntity} containing a page of {@link ResourceRecordView}
     * and HTTP status {@code 200 OK}
     */
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ResourceRecordView>>> getAllRecords(
            @ModelAttribute ResourceRecordViewFilter filter, Pageable pageable) {
        var records = recordViewService.getFilteredRecords(filter, pageable);
        var model = assembler.toModel(records);
        return ResponseEntity.ok(model);
    }

    /**
     * Retrieves a single record by its unique identifier.
     *
     * <p>The method loads a record from the record view service using the provided UUID.
     * If the record exists and the current user has access to it, the record is returned
     * as a response body. Otherwise, an appropriate HTTP error response is returned.</p>
     *
     * @param id unique identifier of the requested record
     * @return {@link ResponseEntity} containing the requested record or an error status
     */
    @SuppressWarnings("unused")
    @GetMapping("/{id}")
    public ResponseEntity<ResourceRecordView> getRecord(@PathVariable UUID id) {
        return ResponseEntity.of(recordViewService.getRecord(id));
    }
}
