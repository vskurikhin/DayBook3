package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import su.svn.api.models.dto.NewMarkdownRecord;
import su.svn.api.models.dto.ResourceMarkdownRecord;
import su.svn.api.models.dto.UpdateMarkdownRecord;
import su.svn.api.repository.client.rest.MarkdownRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static su.svn.lib.Constants.REQUEST_ID;

class MarkdownRecordRepositoryTest {

    @Mock
    MarkdownRecordClient client;

    @Mock
    SecurityContextPrincipalHelper principalHelper;

    @InjectMocks
    MarkdownRecordRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        MDC.put(REQUEST_ID, "req-1");

        when(principalHelper.authorization())
                .thenReturn("Bearer token");
    }

    @Test
    void shouldDeleteRecord() {
        var id = UUID.randomUUID();

        when(client.delete(any(), any(), eq(id)))
                .thenReturn(Uni.createFrom().voidItem());

        repository.delete(id)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();

        verify(client).delete("Bearer token", "req-1", id);
    }

    @Test
    void shouldPostRecord() {
        var dto = mock(NewMarkdownRecord.class);
        var response = mock(ResourceMarkdownRecord.class);

        when(client.post(any(), any(), eq(dto)))
                .thenReturn(Uni.createFrom().item(response));

        repository.post(dto)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();

        verify(client).post("Bearer token", "req-1", dto);
    }

    @Test
    void shouldPutRecord() {
        var dto = mock(UpdateMarkdownRecord.class);
        var response = mock(ResourceMarkdownRecord.class);

        when(client.put(any(), any(), eq(dto)))
                .thenReturn(Uni.createFrom().item(response));

        repository.put(dto)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();

        verify(client).put("Bearer token", "req-1", dto);
    }
}