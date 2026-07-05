package su.svn.core.services;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.BlobRecord;
import su.svn.core.models.dto.NewBlobRecord;
import su.svn.core.models.dto.ResourceBlobRecord;
import su.svn.core.models.dto.UpdateBlobRecord;
import su.svn.core.models.exceptions.CustomNotFoundException;
import su.svn.core.repository.BlobRecordRepository;
import su.svn.core.services.domain.BlobRecordServiceImpl;
import su.svn.core.services.domain.RecordServiceHelper;
import su.svn.core.services.mappers.BlobRecordMapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlobRecordServiceImplTest {

    @Mock
    EntityManager entityManager;

    @Mock
    BlobRecordMapper mapper;

    @Mock
    BlobRecordRepository repository;

    @InjectMocks
    BlobRecordServiceImpl service;

    @Mock
    RecordServiceHelper recordServiceHelper;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();

        BlobRecord entity = BlobRecord.builder().build();
        ResourceBlobRecord resource = ResourceBlobRecord.builder().build();

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.of(entity));

        when(mapper.toResource(entity))
                .thenReturn(resource);

        ResourceBlobRecord result = service.findById(id);

        assertThat(result).isEqualTo(resource);
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(CustomNotFoundException.class);
    }

    @Test
    void shouldDisableRecord() {
        mockAuthentication("root");
        UUID id = UUID.randomUUID();

        BaseRecord baseRecord = BaseRecord.builder()
                .enabled(true)
                .build();

        BlobRecord record = BlobRecord.builder()
                .baseRecord(baseRecord)
                .userName("root")
                .enabled(true)
                .build();

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.of(record));

        service.disable(id);

        assertThat(record.enabled()).isFalse();
        assertThat(record.baseRecord().enabled()).isFalse();

        verify(repository).save(record);
    }

    @Test
    void shouldSaveRecord() {
        mockAuthentication("root");
        NewBlobRecord request = NewBlobRecord.builder()
                .tags(Set.of("tag1"))
                .build();

        ResourceBlobRecord resource = ResourceBlobRecord.builder().build();

        BaseRecord baseRecord = BaseRecord.builder()
                .tags(new ArrayList<>())
                .build();

        BlobRecord entity = BlobRecord.builder()
                .baseRecord(baseRecord)
                .build();

        when(mapper.toResource(request))
                .thenReturn(resource);

        when(mapper.toEntity(resource))
                .thenReturn(entity);

        when(repository.save(entity))
                .thenReturn(entity);

        when(mapper.toResource(entity))
                .thenReturn(resource);

//        when(tagRepository.findByTagIn(any()))
//                .thenReturn(List.of());

//        when(tagRepository.saveAll(any()))
//                .thenAnswer(invocation -> invocation.getArgument(0));

        ResourceBlobRecord result = service.save(request);

        assertThat(result).isEqualTo(resource);

        verify(entityManager).persist(baseRecord);
        verify(entityManager).refresh(baseRecord);
        verify(repository).save(entity);
    }

    @Test
    void shouldUpdateRecord() {
        mockAuthentication("root");
        UUID id = UUID.randomUUID();

        UpdateBlobRecord request = UpdateBlobRecord.builder()
                .id(id)
                .tags(Set.of("tag1"))
                .build();

        BaseRecord existingBaseRecord = BaseRecord.builder()
                .postAt(OffsetDateTime.now())
                .userName("root")
                .tags(new ArrayList<>())
                .build();

        BlobRecord existing = BlobRecord.builder()
                .userName("root")
                .baseRecord(existingBaseRecord)
                .build();

        ResourceBlobRecord resource = ResourceBlobRecord.builder().build();

        BlobRecord updatedEntity = BlobRecord.builder()
                .baseRecord(BaseRecord.builder()
                        .tags(new ArrayList<>())
                        .build())
                .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        when(mapper.toResource(request))
                .thenReturn(resource);

        when(mapper.toEntity(resource))
                .thenReturn(updatedEntity);

//        when(tagRepository.findByTagIn(any()))
//                .thenReturn(List.of());

//        when(tagRepository.saveAll(any()))
//                .thenAnswer(invocation -> invocation.getArgument(0));

        when(repository.save(updatedEntity))
                .thenReturn(updatedEntity);

        when(mapper.toResource(updatedEntity))
                .thenReturn(resource);

        ResourceBlobRecord result = service.update(request);

        assertThat(result).isEqualTo(resource);

        verify(repository).save(updatedEntity);
    }

    @Test
    void shouldThrowAccessDeniedWhenUpdateAnotherUserRecord() {
        mockAuthentication("root");
        UUID id = UUID.randomUUID();

        BlobRecord existing = BlobRecord.builder()
                .userName("another-user")
                .build();

        UpdateBlobRecord request = UpdateBlobRecord.builder()
                .id(id)
                .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.update(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("access denied");
    }

    private void mockAuthentication(String username) {

        when(recordServiceHelper.getUserName())
                .thenReturn("root");
    }
}