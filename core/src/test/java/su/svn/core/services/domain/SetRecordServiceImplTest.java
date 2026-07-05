package su.svn.core.services.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.SetRecord;
import su.svn.core.models.dto.NewSetRecord;
import su.svn.core.models.dto.ResourceSetRecord;
import su.svn.core.models.dto.UpdateSetRecord;
import su.svn.core.models.exceptions.CustomNotFoundException;
import su.svn.core.repository.SetRecordRepository;
import su.svn.core.services.mappers.SetRecordMapper;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetRecordServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private SetRecordMapper setRecordMapper;

    @Mock
    private SetRecordRepository repository;

    @Mock
    private RecordServiceHelper helper;

    @InjectMocks
    private SetRecordServiceImpl service;

    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
    }

    @Test
    void shouldFindById() {
        SetRecord entity = SetRecord.builder()
                .id(id)
                .enabled(true)
                .build();

        ResourceSetRecord resource = ResourceSetRecord.builder()
                .id(id)
                .build();

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.of(entity));

        when(setRecordMapper.toResource(entity))
                .thenReturn(resource);

        ResourceSetRecord result = service.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());

        verify(repository).findByIdAndEnabledTrue(id);
    }

    @Test
    void shouldThrowWhenRecordNotFound() {
        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                () -> service.findById(id));
    }

    @Test
    void shouldDisableRecord() {
        BaseRecord baseRecord = mock(BaseRecord.class);

        SetRecord entity = SetRecord.builder()
                .id(id)
                .userName("user")
                .baseRecord(baseRecord)
                .enabled(true)
                .build();

        when(helper.getUserName()).thenReturn("user");

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.of(entity));

        service.disable(id);

        verify(repository).save(entity);
        verify(baseRecord).enabled(false);
    }

    @Test
    void shouldSaveRecord() {
        NewSetRecord newRecord = NewSetRecord.builder()
                .title("title")
                .texts(Set.of("a"))
                .postAt(OffsetDateTime.now())
                .build();

        ResourceSetRecord resource = ResourceSetRecord.builder()
                .title("title")
                .build();

        BaseRecord baseRecord = BaseRecord.builder().build();

        SetRecord entity = SetRecord.builder()
                .baseRecord(baseRecord)
                .build();

        when(setRecordMapper.toResource(newRecord))
                .thenReturn(resource);

        when(setRecordMapper.toEntity(resource))
                .thenReturn(entity);

        when(helper.getUserName()).thenReturn("user");

        when(repository.save(entity)).thenReturn(entity);

        when(setRecordMapper.toResource(entity))
                .thenReturn(resource);

        ResourceSetRecord result = service.save(newRecord);

        assertNotNull(result);

        verify(entityManager).persist(baseRecord);
        verify(entityManager).refresh(baseRecord);
        verify(repository).save(entity);
    }

    @Test
    void shouldUpdateRecord() {
        BaseRecord baseRecord = BaseRecord.builder()
                .postAt(OffsetDateTime.now())
                .userName("user")
                .build();

        SetRecord existing = SetRecord.builder()
                .id(id)
                .userName("user")
                .baseRecord(baseRecord)
                .build();

        UpdateSetRecord update = UpdateSetRecord.builder()
                .id(id)
                .title("updated")
                .build();

        ResourceSetRecord resource = ResourceSetRecord.builder()
                .id(id)
                .title("updated")
                .build();

        SetRecord entity = SetRecord.builder()
                .baseRecord(BaseRecord.builder().build())
                .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        when(helper.getUserName())
                .thenReturn("user");

        when(setRecordMapper.toResource(update))
                .thenReturn(resource);

        when(setRecordMapper.toEntity(resource))
                .thenReturn(entity);

        when(repository.save(entity))
                .thenReturn(entity);

        when(setRecordMapper.toResource(entity))
                .thenReturn(resource);

        ResourceSetRecord result = service.update(update);

        assertNotNull(result);

        verify(repository).save(entity);
    }

    @Test
    void shouldThrowAccessDeniedOnUpdate() {
        SetRecord existing = SetRecord.builder()
                .id(id)
                .userName("owner")
                .build();

        UpdateSetRecord update = UpdateSetRecord.builder()
                .id(id)
                .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        when(helper.getUserName())
                .thenReturn("another-user");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.update(update)
        );

        assertEquals("access denied", exception.getMessage());
    }
}