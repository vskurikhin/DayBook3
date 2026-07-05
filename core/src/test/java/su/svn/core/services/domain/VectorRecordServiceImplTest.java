package su.svn.core.services.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.VectorRecord;
import su.svn.core.models.dto.NewVectorRecord;
import su.svn.core.models.dto.ResourceVectorRecord;
import su.svn.core.models.dto.UpdateVectorRecord;
import su.svn.core.repository.VectorRecordRepository;
import su.svn.core.services.mappers.VectorRecordMapper;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VectorRecordServiceImplTest {

    @Mock
    EntityManager entityManager;

    @Mock
    VectorRecordMapper mapper;

    @Mock
    VectorRecordRepository repository;

    @Mock
    RecordServiceHelper helper;

    @InjectMocks
    VectorRecordServiceImpl service;

    UUID id;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID();
    }

    @Test
    void shouldFindById() {

        VectorRecord entity = VectorRecord.builder()
                .id(id)
                .build();

        ResourceVectorRecord resource =
                ResourceVectorRecord.builder()
                        .id(id)
                        .build();

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.of(entity));

        when(mapper.toResource(entity))
                .thenReturn(resource);

        ResourceVectorRecord result = service.findById(id);

        assertEquals(id, result.id());
    }

    @Test
    void shouldDisableRecord() {

        BaseRecord baseRecord = BaseRecord.builder().build();

        VectorRecord entity = VectorRecord.builder()
                .id(id)
                .baseRecord(baseRecord)
                .userName("root")
                .enabled(true)
                .build();

        when(helper.getUserName()).thenReturn("root");

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.of(entity));

        service.disable(id);

        assertFalse(entity.enabled());
        verify(repository).save(entity);
    }

    @Test
    void shouldSaveRecord() {

        NewVectorRecord newRecord =
                NewVectorRecord.builder()
                        .title("vector")
                        .vector(new float[]{1.0f})
                        .tags(Set.of("ai"))
                        .postAt(OffsetDateTime.now())
                        .build();

        ResourceVectorRecord resource =
                ResourceVectorRecord.builder()
                        .title("vector")
                        .vector(new float[]{1.0f})
                        .tags(Set.of("ai"))
                        .build();

        BaseRecord baseRecord = BaseRecord.builder().build();

        VectorRecord entity = VectorRecord.builder()
                .baseRecord(baseRecord)
                .build();

        when(mapper.toResource(newRecord)).thenReturn(resource);
        when(mapper.toEntity(resource)).thenReturn(entity);
        when(helper.getUserName()).thenReturn("root");
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResource(entity)).thenReturn(resource);

        ResourceVectorRecord result = service.save(newRecord);

        assertEquals(resource.title(), result.title());

        verify(entityManager).persist(baseRecord);
        verify(entityManager).refresh(baseRecord);
    }

    @Test
    void shouldUpdateRecord() {

        UpdateVectorRecord updateRecord =
                UpdateVectorRecord.builder()
                        .id(id)
                        .vector(new float[]{2.0f})
                        .build();

        BaseRecord baseRecord = BaseRecord.builder()
                .userName("root")
                .postAt(OffsetDateTime.now())
                .build();

        VectorRecord stored = VectorRecord.builder()
                .id(id)
                .baseRecord(baseRecord)
                .userName("root")
                .build();

        ResourceVectorRecord resource =
                ResourceVectorRecord.builder()
                        .id(id)
                        .vector(new float[]{2.0f})
                        .build();

        VectorRecord mapped = VectorRecord.builder()
                .id(id)
                .baseRecord(BaseRecord.builder().build())
                .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(stored));

        when(helper.getUserName())
                .thenReturn("root");

        when(mapper.toResource(updateRecord))
                .thenReturn(resource);

        when(mapper.toEntity(resource))
                .thenReturn(mapped);

        when(repository.save(mapped))
                .thenReturn(mapped);

        when(mapper.toResource(mapped))
                .thenReturn(resource);

        ResourceVectorRecord result =
                service.update(updateRecord);

        assertEquals(id, result.id());

        verify(repository).save(mapped);
    }
}