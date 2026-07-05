/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * MarkdownRecordServiceImpl.java
 * $Id$
 */

package su.svn.core.services.domain;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.svn.core.models.dto.NewMarkdownRecord;
import su.svn.core.models.dto.ResourceMarkdownRecord;
import su.svn.core.models.dto.UpdateMarkdownRecord;
import su.svn.core.models.exceptions.CustomNotFoundException;
import su.svn.core.repository.TextRecordRepository;
import su.svn.core.services.mappers.MarkdownRecordMapper;
import su.svn.lib.RecordType;
import su.svn.lib.TextRecordType;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Service implementation for markdown record management.
 * <p>
 * Handles:
 * <ul>
 *     <li>creation of markdown records</li>
 *     <li>updating existing records</li>
 *     <li>loading records by identifier</li>
 *     <li>logical deletion of records</li>
 *     <li>tag synchronization</li>
 * </ul>
 * </p>
 *
 * <p>
 * Access control is based on the currently authenticated user.
 * Only record owners may modify or disable records.
 * </p>
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class MarkdownRecordServiceImpl implements MarkdownRecordService {

    EntityManager entityManager;

    MarkdownRecordMapper mapper;
    TextRecordRepository repository;

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
    public ResourceMarkdownRecord findById(UUID id) {
        return mapper.toResource(
                repository.findByIdAndEnabledTrue(id)
                        .orElseThrow(CustomNotFoundException::new)
        );
    }

    @Override
    @Transactional
    public ResourceMarkdownRecord save(NewMarkdownRecord newRecord) {
        var resourceRecord = mapper.toResource(newRecord);
        var record = mapper.toEntity(resourceRecord);
        final String username = recordServiceHelper.getUserName();
        record.baseRecord().type(RecordType.Text);
        record.baseRecord().userName(username);
        record.userName(username);
        record.type(TextRecordType.Markdown);
        var baseRecord = record.baseRecord();
        recordServiceHelper.upTagsInBaseRecordFromDB(baseRecord, newRecord.tags(), username);
        entityManager.persist(baseRecord);
        entityManager.refresh(baseRecord);
        return mapper.toResource(repository.save(record));
    }

    @Override
    @Transactional
    public ResourceMarkdownRecord update(UpdateMarkdownRecord updateRecord) {
        var optionalRecord = repository.findById(updateRecord.id());
        final String username = recordServiceHelper.getUserName();
        if (username.equals(optionalRecord.orElseThrow().userName())) {
            var resourceRecord = mapper.toResource(updateRecord);
            var record = mapper.toEntity(resourceRecord);
            record.baseRecord()
                    .type(RecordType.Text);
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
            record.type(TextRecordType.Markdown);
            var baseRecord = record.baseRecord();
            recordServiceHelper.upTagsInBaseRecordFromDB(baseRecord, updateRecord.tags(), username);
            return mapper.toResource(repository.save(record));
        }
        throw new RuntimeException("access denied");
    }
}
