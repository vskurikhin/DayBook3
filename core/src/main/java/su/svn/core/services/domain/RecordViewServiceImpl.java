/*
 * This file was last modified at 2026.07.01 22:56 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordViewServiceImpl.java
 * $Id$
 */

package su.svn.core.services.domain;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import su.svn.core.domain.entities.RecordView;
import su.svn.lib.models.dto.ResourceRecordView;
import su.svn.core.models.dto.ResourceRecordViewFilter;
import su.svn.core.repository.RecordViewRepository;
import su.svn.core.repository.specifications.RecordViewSpecificationBuilder;
import su.svn.core.services.mappers.RecordViewMapper;

import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Default implementation of {@link RecordViewService}.
 *
 * <p>This service provides business operations for retrieving
 * {@link RecordView} entities and converting them into
 * {@link ResourceRecordView} DTO representations.</p>
 *
 * <p>The service supports:
 * <ul>
 *     <li>Retrieving a single record by its unique identifier;</li>
 *     <li>Filtering records using {@link ResourceRecordViewFilter};</li>
 *     <li>Pagination and sorting using {@link Pageable};</li>
 *     <li>Mapping database entities to API resources using {@link RecordViewMapper}.</li>
 * </ul>
 * </p>
 *
 * <p>Filtering logic is delegated to {@link RecordViewSpecificationBuilder},
 * which creates a dynamic {@link Specification} used by the repository.</p>
 *
 * @author Victor N. Skurikhin
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class RecordViewServiceImpl implements RecordViewService {

    RecordViewRepository recordViewRepository;
    RecordViewSpecificationBuilder specificationBuilder;
    RecordViewMapper recordViewMapper;

    @Override
    public Optional<ResourceRecordView> getRecord(UUID id) {
        return recordViewRepository.findById(id).map(recordViewMapper::toResource);
    }

    @Override
    public Page<ResourceRecordView> getFilteredRecords(ResourceRecordViewFilter filter, Pageable pageable) {
        Specification<RecordView> specification = specificationBuilder.build(filter);
        Page<RecordView> records = recordViewRepository.findAll(specification, pageable);

        return records.map(recordViewMapper::toResource);
    }
}
