/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecordServiceImpl.java
 * $Id$
 */

package su.svn.core.services.domain;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.svn.core.models.dto.NewJsonRecord;
import su.svn.core.models.dto.ResourceJsonRecord;
import su.svn.core.models.dto.UpdateJsonRecord;
import su.svn.core.models.exceptions.CustomNotFoundException;
import su.svn.core.repository.JsonRecordRepository;
import su.svn.core.services.mappers.JsonRecordMapper;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Implementation of {@link JsonRecordService}.
 *
 * <p>Handles business logic for JSON records, including persistence
 * and mapping between entities and DTOs.</p>
 *
 * @author Victor N. Skurikhin
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class JsonRecordServiceImpl implements JsonRecordService {

    EntityManager entityManager;

    JsonRecordMapper mapper;
    JsonRecordRepository repository;

    RecordServiceHelper recordServiceHelper;

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

    @Override
    public ResourceJsonRecord findById(UUID id) {
        return mapper.toResource(
                repository.findByIdAndEnabledTrue(id)
                        .orElseThrow(CustomNotFoundException::new)
        );
    }

    @Override
    @Transactional
    public ResourceJsonRecord save(NewJsonRecord newRecord) {
        var resourceJsonRecord = mapper.toResource(newRecord);
        var jsonRecord = mapper.toEntity(resourceJsonRecord);
        final String username = recordServiceHelper.getUserName();
        jsonRecord.baseRecord().type(su.svn.lib.RecordType.Json);
        jsonRecord.baseRecord().userName(username);
        jsonRecord.userName(username);
        var baseRecord = jsonRecord.baseRecord();
        recordServiceHelper.upTagsInBaseRecordFromDB(baseRecord, newRecord.tags(), username);
        entityManager.persist(baseRecord);
        entityManager.refresh(baseRecord);
        return mapper.toResource(repository.save(jsonRecord));
    }

    @Override
    @Transactional
    public ResourceJsonRecord update(UpdateJsonRecord updateRecord) {
        var optionalJsonRecord = repository.findById(updateRecord.id());
        final String username = recordServiceHelper.getUserName();
        if (username.equals(optionalJsonRecord.orElseThrow().userName())) {
            var resourceJsonRecord = mapper.toResource(updateRecord);
            var jsonRecord = mapper.toEntity(resourceJsonRecord);
            jsonRecord.baseRecord()
                    .type(su.svn.lib.RecordType.Json);
            jsonRecord.baseRecord()
                    .postAt(optionalJsonRecord.orElseThrow()
                            .baseRecord()
                            .postAt()
                    );
            jsonRecord.baseRecord()
                    .userName(optionalJsonRecord.orElseThrow()
                            .baseRecord()
                            .userName()
                    );
            jsonRecord.userName(username);
            var baseRecord = jsonRecord.baseRecord();
            recordServiceHelper.upTagsInBaseRecordFromDB(baseRecord, updateRecord.tags(), username);
            return mapper.toResource(repository.save(jsonRecord));
        }
        throw new RuntimeException("access denied");
    }
}
