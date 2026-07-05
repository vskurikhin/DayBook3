/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BlobRecordServiceImpl.java
 * $Id$
 */

package su.svn.core.services.domain;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.svn.core.models.dto.NewBlobRecord;
import su.svn.core.models.dto.ResourceBlobRecord;
import su.svn.core.models.dto.UpdateBlobRecord;
import su.svn.core.models.exceptions.CustomNotFoundException;
import su.svn.core.repository.BlobRecordRepository;
import su.svn.core.services.mappers.BlobRecordMapper;
import su.svn.lib.RecordType;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Сервис для работы с BlobRecord.
 * <p>
 * Выполняет:
 * <ul>
 *     <li>создание записей</li>
 *     <li>обновление записей</li>
 *     <li>поиск записей</li>
 *     <li>логическое удаление записей</li>
 *     <li>обработку тегов</li>
 * </ul>
 * </p>
 *
 * <p>
 * Сервис использует Spring Security для получения текущего пользователя.
 * </p>
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class BlobRecordServiceImpl implements BlobRecordService {

    EntityManager entityManager;

    BlobRecordMapper mapper;
    BlobRecordRepository repository;

    RecordServiceHelper recordServiceHelper;

    /**
     * Выполняет логическое удаление записи.
     * <p>
     * Запись отключается только если текущий пользователь
     * является владельцем записи.
     * </p>
     *
     * @param id идентификатор записи
     * @throws CustomNotFoundException если запись не найдена
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
     * Возвращает запись по идентификатору.
     *
     * @param id идентификатор записи
     * @return ресурс записи
     * @throws CustomNotFoundException если запись не найдена
     */
    @Override
    public ResourceBlobRecord findById(UUID id) {
        return mapper.toResource(
                repository.findByIdAndEnabledTrue(id)
                        .orElseThrow(CustomNotFoundException::new)
        );
    }

    /**
     * Создает новую запись.
     *
     * @param newRecord данные новой записи
     * @return сохраненная запись
     */
    @Override
    @Transactional
    public ResourceBlobRecord save(NewBlobRecord newRecord) {
        var resourceBlobRecord = mapper.toResource(newRecord);
        var record = mapper.toEntity(resourceBlobRecord);
        final String username = recordServiceHelper.getUserName();
        record.baseRecord().type(RecordType.Blob);
        record.baseRecord().userName(username);
        record.userName(username);
        var baseRecord = record.baseRecord();
        recordServiceHelper.upTagsInBaseRecordFromDB(baseRecord, newRecord.tags(), username);
        entityManager.persist(baseRecord);
        entityManager.refresh(baseRecord);
        return mapper.toResource(repository.save(record));
    }

    /**
     * Обновляет существующую запись.
     *
     * @param updateRecord данные обновления
     * @return обновленная запись
     * @throws RuntimeException если пользователь не является владельцем записи
     */
    @Override
    @Transactional
    public ResourceBlobRecord update(UpdateBlobRecord updateRecord) {
        var optionalBlobRecord = repository.findById(updateRecord.id());
        final String username = recordServiceHelper.getUserName();
        if (username.equals(optionalBlobRecord.orElseThrow().userName())) {
            var resourceBlobRecord = mapper.toResource(updateRecord);
            var record = mapper.toEntity(resourceBlobRecord);
            record.baseRecord()
                    .type(RecordType.Blob);
            record.baseRecord()
                    .postAt(optionalBlobRecord.orElseThrow()
                            .baseRecord()
                            .postAt()
                    );
            record.baseRecord()
                    .userName(optionalBlobRecord.orElseThrow()
                            .baseRecord()
                            .userName()
                    );
            record.userName(username);
            var baseRecord = record.baseRecord();
            recordServiceHelper.upTagsInBaseRecordFromDB(baseRecord, updateRecord.tags(), username);
            return mapper.toResource(repository.save(record));
        }
        throw new RuntimeException("access denied");
    }
}
