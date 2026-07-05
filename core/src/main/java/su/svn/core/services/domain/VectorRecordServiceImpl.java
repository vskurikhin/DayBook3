/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecordServiceImpl.java
 * $Id$
 */

package su.svn.core.services.domain;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.svn.core.models.dto.NewVectorRecord;
import su.svn.core.models.dto.ResourceVectorRecord;
import su.svn.core.models.dto.UpdateVectorRecord;
import su.svn.core.models.exceptions.CustomNotFoundException;
import su.svn.core.repository.VectorRecordRepository;
import su.svn.core.services.mappers.VectorRecordMapper;
import su.svn.lib.RecordType;
import su.svn.lib.TextRecordType;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Default implementation of {@link VectorRecordService}.
 *
 * <p>
 * Provides business logic for vector record management,
 * including creation, update, retrieval, and logical deletion.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Mapping DTO objects to entities and back</li>
 *     <li>Managing ownership and access validation</li>
 *     <li>Persisting vector records and related base records</li>
 *     <li>Managing associated tags</li>
 * </ul>
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class VectorRecordServiceImpl implements VectorRecordService {

    EntityManager entityManager;

    VectorRecordMapper mapper;
    VectorRecordRepository repository;

    RecordServiceHelper recordServiceHelper;

    /**
     * Disables a vector record by identifier.
     *
     * <p>
     * The record is logically deleted and becomes unavailable
     * for active queries.
     * </p>
     *
     * @param id unique identifier of the vector record
     */
    @Override
    @Transactional
    public void disable(UUID id) {
        var username = recordServiceHelper.getUserName();
        var record = repository.findByIdAndEnabledTrue(id)
                .orElseThrow(CustomNotFoundException::new);
        if (username.equals(record.userName())) {
            record.baseRecord().enabled(false);
            record.enabled(false);
            repository.save(record);
        }
    }

    /**
     * Finds an active vector record by identifier.
     *
     * @param id unique identifier of the vector record
     * @return vector record resource DTO
     * @throws CustomNotFoundException if record does not exist
     */
    @Override
    public ResourceVectorRecord findById(UUID id) {
        return mapper.toResource(
                repository.findByIdAndEnabledTrue(id)
                        .orElseThrow(CustomNotFoundException::new)
        );
    }

    /**
     * Creates and stores a new vector record.
     *
     * @param newRecord DTO containing vector record data
     * @return saved vector record as resource DTO
     */
    @Override
    @Transactional
    public ResourceVectorRecord save(NewVectorRecord newRecord) {
        var resourceRecord = mapper.toResource(newRecord);
        var record = mapper.toEntity(resourceRecord);
        final String username = recordServiceHelper.getUserName();
        record.baseRecord().type(RecordType.Vector);
        record.baseRecord().userName(username);
        record.userName(username);
        record.type(TextRecordType.Value);
        var baseRecord = record.baseRecord();
        recordServiceHelper.upTagsInBaseRecordFromDB(baseRecord, newRecord.tags(), username);
        entityManager.persist(baseRecord);
        entityManager.refresh(baseRecord);
        return mapper.toResource(repository.save(record));
    }

    /**
     * Updates an existing vector record.
     *
     * <p>
     * Only the owner of the record is allowed to perform updates.
     * </p>
     *
     * @param updateRecord DTO containing updated vector record data
     * @return updated vector record as resource DTO
     * @throws RuntimeException if access is denied
     */
    @Override
    @Transactional
    public ResourceVectorRecord update(UpdateVectorRecord updateRecord) {
        var optionalRecord = repository.findById(updateRecord.id());
        final String username = recordServiceHelper.getUserName();
        if (username.equals(optionalRecord.orElseThrow().userName())) {
            var resourceRecord = mapper.toResource(updateRecord);
            var record = mapper.toEntity(resourceRecord);
            record.baseRecord()
                    .type(RecordType.Vector);
            record.baseRecord()
                    .postAt(optionalRecord.orElseThrow()
                            .baseRecord()
                            .postAt()
                    );
            record.baseRecord()
                    .userName(optionalRecord.orElseThrow()
                            .baseRecord()
                            .userName()
                    );
            record.userName(username);
            record.type(TextRecordType.Value);
            var baseRecord = record.baseRecord();
            recordServiceHelper.upTagsInBaseRecordFromDB(baseRecord, updateRecord.tags(), username);
            return mapper.toResource(repository.save(record));
        }
        throw new RuntimeException("access denied");
    }
}
