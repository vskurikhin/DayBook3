package su.svn.core.services.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.TextRecord;
import su.svn.core.models.dto.NewValueRecord;
import su.svn.core.models.dto.ResourceValueRecord;
import su.svn.core.models.dto.UpdateValueRecord;
import su.svn.core.models.exceptions.CustomNotFoundException;
import su.svn.core.repository.TextRecordRepository;
import su.svn.core.services.mappers.ValueRecordMapper;
import su.svn.lib.RecordType;
import su.svn.lib.TextRecordType;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValueRecordServiceImplTest {

    @Mock
    EntityManager entityManager;

    @Mock
    ValueRecordMapper mapper;

    @Mock
    TextRecordRepository repository;

    @Mock
    RecordServiceHelper helper;

    @InjectMocks
    ValueRecordServiceImpl service;

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();

        TextRecord entity = TextRecord.builder().build();
        ResourceValueRecord resource =
                ResourceValueRecord.builder().build();

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.of(entity));

        when(mapper.toResource(entity))
                .thenReturn(resource);

        ResourceValueRecord result = service.findById(id);

        assertThat(result).isEqualTo(resource);
    }

    @Test
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(CustomNotFoundException.class);
    }

    @Test
    void shouldDisableRecord() {
        when(helper.getUserName()).thenReturn("root");
        UUID id = UUID.randomUUID();

        BaseRecord baseRecord = BaseRecord.builder()
                .enabled(true)
                .build();

        TextRecord entity = TextRecord.builder()
                .baseRecord(baseRecord)
                .userName("root")
                .enabled(true)
                .build();

        when(repository.findByIdAndEnabledTrue(id))
                .thenReturn(Optional.of(entity));

        service.disable(id);

        assertThat(entity.enabled()).isFalse();
        assertThat(entity.baseRecord().enabled()).isFalse();

        verify(repository).save(entity);
    }

    @Test
    void shouldSaveRecord() {
        when(helper.getUserName()).thenReturn("root");
        NewValueRecord request = NewValueRecord.builder()
                .value("# value")
                .tags(Set.of("tag"))
                .build();

        ResourceValueRecord resource =
                ResourceValueRecord.builder().build();

        BaseRecord baseRecord = BaseRecord.builder().build();

        TextRecord entity = TextRecord.builder()
                .baseRecord(baseRecord)
                .build();

        when(mapper.toResource(request)).thenReturn(resource);
        when(mapper.toEntity(resource)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResource(entity)).thenReturn(resource);

        ResourceValueRecord result = service.save(request);

        assertThat(result).isEqualTo(resource);

        assertThat(entity.type()).isEqualTo(TextRecordType.Value);
        assertThat(entity.baseRecord().type()).isEqualTo(RecordType.Text);

        verify(entityManager).persist(baseRecord);
        verify(entityManager).refresh(baseRecord);
        verify(repository).save(entity);
    }

    @Test
    void shouldUpdateRecord() {
        when(helper.getUserName()).thenReturn("root");
        UUID id = UUID.randomUUID();

        UpdateValueRecord request = UpdateValueRecord.builder()
                .id(id)
                .value("updated")
                .tags(Set.of("tag"))
                .build();

        BaseRecord existingBase = BaseRecord.builder()
                .postAt(OffsetDateTime.now())
                .userName("root")
                .build();

        TextRecord existing = TextRecord.builder()
                .userName("root")
                .baseRecord(existingBase)
                .build();

        ResourceValueRecord resource =
                ResourceValueRecord.builder().build();

        TextRecord updated = TextRecord.builder()
                .baseRecord(BaseRecord.builder().build())
                .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        when(mapper.toResource(request)).thenReturn(resource);
        when(mapper.toEntity(resource)).thenReturn(updated);
        when(repository.save(updated)).thenReturn(updated);
        when(mapper.toResource(updated)).thenReturn(resource);

        ResourceValueRecord result = service.update(request);

        assertThat(result).isEqualTo(resource);

        assertThat(updated.type())
                .isEqualTo(TextRecordType.Value);

        verify(repository).save(updated);
    }

    @Test
    void shouldThrowAccessDenied() {
        when(helper.getUserName()).thenReturn("root");
        UUID id = UUID.randomUUID();

        when(helper.getUserName())
                .thenReturn("root");

        TextRecord entity = TextRecord.builder()
                .userName("another")
                .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(entity));

        UpdateValueRecord request =
                UpdateValueRecord.builder()
                        .id(id)
                        .build();

        assertThatThrownBy(() -> service.update(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("access denied");
    }
}