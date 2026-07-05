package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.models.dto.NewMarkdownRecord;
import su.svn.api.models.dto.ResourceMarkdownRecord;
import su.svn.api.models.dto.UpdateMarkdownRecord;
import su.svn.api.repository.MarkdownRecordRepository;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkdownRecordDataServiceTest {

    @InjectMocks
    MarkdownRecordDataService service;

    @Mock
    MarkdownRecordRepository repository;

    @Mock
    MarkdownRecordSyncTrigger trigger;

    @Test
    void shouldPostMarkdownRecord() {

        NewMarkdownRecord request = mock(NewMarkdownRecord.class);
        ResourceMarkdownRecord response = mock(ResourceMarkdownRecord.class);

        when(repository.post(request))
                .thenReturn(Uni.createFrom().item(response));

        ResourceMarkdownRecord result = service.post(request)
                .await()
                .indefinitely();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(response);

        verify(repository).post(request);
        verify(trigger).accept(response);
    }

    @Test
    void shouldUpdateMarkdownRecord() {

        UpdateMarkdownRecord request = mock(UpdateMarkdownRecord.class);
        ResourceMarkdownRecord response = mock(ResourceMarkdownRecord.class);

        when(repository.put(request))
                .thenReturn(Uni.createFrom().item(response));

        ResourceMarkdownRecord result = service.put(request)
                .await()
                .indefinitely();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(response);

        verify(repository).put(request);
        verify(trigger).accept(response);
    }

    @Test
    void shouldDeleteMarkdownRecord() {

        UUID id = UUID.randomUUID();

        when(repository.delete(id))
                .thenReturn(Uni.createFrom().voidItem());

        service.delete(id)
                .await()
                .indefinitely();

        verify(repository).delete(id);
    }

    @Test
    void shouldFailWhenPostFails() {

        RuntimeException exception =
                new RuntimeException("remote failure");

        NewMarkdownRecord request = mock(NewMarkdownRecord.class);

        when(repository.post(request))
                .thenReturn(Uni.createFrom().failure(exception));

        assertThatThrownBy(() ->
                service.post(request)
                        .await()
                        .indefinitely()
        ).isEqualTo(exception);

        verify(repository).post(request);
        verifyNoInteractions(trigger);
    }

    @Test
    void shouldFailWhenPutFails() {

        RuntimeException exception =
                new RuntimeException("remote failure");

        UpdateMarkdownRecord request =
                mock(UpdateMarkdownRecord.class);

        when(repository.put(request))
                .thenReturn(Uni.createFrom().failure(exception));

        assertThatThrownBy(() ->
                service.put(request)
                        .await()
                        .indefinitely()
        ).isEqualTo(exception);

        verify(repository).put(request);
        verifyNoInteractions(trigger);
    }
}
