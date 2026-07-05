package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.models.dto.NewVectorRecord;
import su.svn.api.models.dto.ResourceVectorRecord;
import su.svn.api.models.dto.UpdateVectorRecord;
import su.svn.api.repository.VectorRecordRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VectorRecordDataServiceTest {

    @InjectMocks
    VectorRecordDataService service;

    @Mock
    VectorRecordSyncTrigger trigger;

    @Mock
    VectorRecordRepository repository;

    @Test
    void shouldDeleteRecord() {
        var id = UUID.randomUUID();

        when(repository.delete(id))
                .thenReturn(Uni.createFrom().voidItem());

        service.delete(id)
                .await()
                .indefinitely();

        verify(repository).delete(id);
    }

    @Test
    void shouldPostRecord() {
        var request = NewVectorRecord.builder()
                .parentId(UUID.randomUUID())
                .title("vector")
                .vector(new float[]{1.0f, 2.0f})
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(5)
                .build();

        var response = ResourceVectorRecord.builder()
                .id(UUID.randomUUID())
                .title("vector")
                .vector(new float[]{1.0f, 2.0f})
                .visible(true)
                .flags(5)
                .build();

        when(repository.post(request))
                .thenReturn(Uni.createFrom().item(response));

        var result = service.post(request)
                .await()
                .indefinitely();

        assertThat(result).isEqualTo(response);

        verify(repository).post(request);
        verify(trigger).accept(response);
    }

    @Test
    void shouldPutRecord() {
        UpdateVectorRecord request = mock(UpdateVectorRecord.class);
        ResourceVectorRecord response = mock(ResourceVectorRecord.class);

        when(repository.put(request))
                .thenReturn(Uni.createFrom().item(response));

        var result = service.put(request)
                .await()
                .indefinitely();

        assertThat(result).isEqualTo(response);

        verify(repository).put(request);
        verify(trigger).accept(response);
    }
}