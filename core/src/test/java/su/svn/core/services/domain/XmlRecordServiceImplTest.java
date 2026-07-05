package su.svn.core.services.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.XmlRecord;
import su.svn.core.models.dto.NewXmlRecord;
import su.svn.core.models.dto.ResourceXmlRecord;
import su.svn.core.models.dto.UpdateXmlRecord;
import su.svn.core.models.exceptions.CustomNotFoundException;
import su.svn.core.repository.XmlRecordRepository;
import su.svn.core.services.mappers.XmlRecordMapper;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class XmlRecordServiceImplTest {

    @Mock
    EntityManager entityManager;

    @Mock
    XmlRecordMapper mapper;

    @Mock
    XmlRecordRepository repository;

    @Mock
    RecordServiceHelper recordServiceHelper;

    @InjectMocks
    XmlRecordServiceImpl service;

    UUID id;
    XmlRecord entity;
    ResourceXmlRecord resource;

    @BeforeEach
    void setup() {

        id = UUID.randomUUID();

        BaseRecord baseRecord = BaseRecord.builder()
                .id(id)
                .build();

        entity = XmlRecord.builder()
                .id(id)
                .baseRecord(baseRecord)
                .userName("user")
                .enabled(true)
                .build();

        resource = ResourceXmlRecord.builder()
                .id(id)
                .xml("<xml/>")
                .postAt(OffsetDateTime.now())
                .tags(Set.of("tag"))
                .build();
    }

    @Test
    void findById_shouldReturnResource() {

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.of(entity));

        when(mapper.toResource(entity))
                .thenReturn(resource);

        ResourceXmlRecord result = service.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());

        verify(repository).findByIdAndEnabledTrue(id);
        verify(mapper).toResource(entity);
    }

    @Test
    void findById_shouldThrowException() {

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                () -> service.findById(id));
    }

    @Test
    void disable_shouldDisableRecord() {

        when(recordServiceHelper.getUserName())
                .thenReturn("user");

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.of(entity));

        service.disable(id);

        assertFalse(entity.enabled());
        assertFalse(entity.baseRecord().enabled());

        verify(repository).save(entity);
    }

    @Test
    void save_shouldPersistRecord() {

        NewXmlRecord newRecord = NewXmlRecord.builder()
                .xml("<xml/>")
                .tags(Set.of("tag"))
                .build();

        when(recordServiceHelper.getUserName())
                .thenReturn("user");

        when(mapper.toResource(newRecord))
                .thenReturn(resource);

        when(mapper.toEntity(resource))
                .thenReturn(entity);

        when(repository.save(entity))
                .thenReturn(entity);

        when(mapper.toResource(entity))
                .thenReturn(resource);

        ResourceXmlRecord result = service.save(newRecord);

        assertNotNull(result);

        verify(entityManager).persist(entity.baseRecord());
        verify(entityManager).refresh(entity.baseRecord());
        verify(repository).save(entity);
    }

    @Test
    void update_shouldUpdateRecord() {

        UpdateXmlRecord updateRecord = UpdateXmlRecord.builder()
                .id(id)
                .xml("<updated/>")
                .tags(Set.of("tag"))
                .build();

        when(recordServiceHelper.getUserName())
                .thenReturn("user");

        when(repository.findById(id))
                .thenReturn(Optional.of(entity));

        when(mapper.toResource(updateRecord))
                .thenReturn(resource);

        when(mapper.toEntity(resource))
                .thenReturn(entity);

        when(repository.save(entity))
                .thenReturn(entity);

        when(mapper.toResource(entity))
                .thenReturn(resource);

        ResourceXmlRecord result = service.update(updateRecord);

        assertNotNull(result);

        verify(repository).save(entity);
    }

    @Test
    void update_shouldThrowExceptionWhenAccessDenied() {

        UpdateXmlRecord updateRecord = UpdateXmlRecord.builder()
                .id(id)
                .xml("<updated/>")
                .build();

        when(recordServiceHelper.getUserName())
                .thenReturn("another-user");

        when(repository.findById(id))
                .thenReturn(Optional.of(entity));

        assertThrows(RuntimeException.class,
                () -> service.update(updateRecord));
    }
}