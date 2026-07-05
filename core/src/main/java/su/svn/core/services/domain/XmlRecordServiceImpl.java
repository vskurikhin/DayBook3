/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * XmlRecordServiceImpl.java
 * $Id$
 */

package su.svn.core.services.domain;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.svn.core.models.dto.NewXmlRecord;
import su.svn.core.models.dto.ResourceXmlRecord;
import su.svn.core.models.dto.UpdateXmlRecord;
import su.svn.core.models.exceptions.CustomNotFoundException;
import su.svn.core.repository.XmlRecordRepository;
import su.svn.core.services.mappers.XmlRecordMapper;
import su.svn.lib.RecordType;
import su.svn.lib.TextRecordType;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Default implementation of {@link XmlRecordService}.
 *
 * <p>
 * Provides transactional operations for XML record persistence,
 * retrieval, updating, and logical deletion.
 * </p>
 *
 * <h2>Main Responsibilities</h2>
 * <ul>
 *     <li>Managing XML record lifecycle</li>
 *     <li>Applying ownership validation</li>
 *     <li>Updating related {@link su.svn.core.domain.entities.BaseRecord} metadata</li>
 *     <li>Handling tag synchronization</li>
 * </ul>
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class XmlRecordServiceImpl implements XmlRecordService {

    EntityManager entityManager;

    XmlRecordMapper mapper;
    XmlRecordRepository repository;

    RecordServiceHelper recordServiceHelper;

    /**
     * Disables an XML record if the current user is the owner.
     *
     * @param id unique XML record identifier
     * @throws CustomNotFoundException if the record does not exist
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
     * Finds an active XML record by identifier.
     *
     * @param id unique XML record identifier
     * @return found XML record resource
     * @throws CustomNotFoundException if the record does not exist
     */
    @Override
    public ResourceXmlRecord findById(UUID id) {
        return mapper.toResource(
                repository.findByIdAndEnabledTrue(id)
                        .orElseThrow(CustomNotFoundException::new)
        );
    }

    /**
     * Creates and persists a new XML record.
     *
     * <p>
     * Initializes ownership, base record metadata,
     * and associated tags before persistence.
     * </p>
     *
     * @param newRecord DTO containing XML record creation data
     * @return persisted XML record resource
     */
    @Override
    @Transactional
    public ResourceXmlRecord save(NewXmlRecord newRecord) {
        var resourceRecord = mapper.toResource(newRecord);
        var record = mapper.toEntity(resourceRecord);
        final String username = recordServiceHelper.getUserName();
        record.baseRecord().type(RecordType.Xml);
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
     * Updates an existing XML record.
     *
     * <p>
     * Only the owner of the record is allowed to perform updates.
     * Existing immutable metadata is preserved.
     * </p>
     *
     * @param updateRecord DTO containing updated XML record data
     * @return updated XML record resource
     * @throws RuntimeException if access is denied
     */
    @Override
    @Transactional
    public ResourceXmlRecord update(UpdateXmlRecord updateRecord) {
        var optionalRecord = repository.findById(updateRecord.id());
        final String username = recordServiceHelper.getUserName();
        if (username.equals(optionalRecord.orElseThrow().userName())) {
            var resourceRecord = mapper.toResource(updateRecord);
            var record = mapper.toEntity(resourceRecord);
            record.baseRecord()
                    .type(RecordType.Xml);
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
