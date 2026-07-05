package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import su.svn.api.models.dto.NewValueRecord;
import su.svn.api.models.dto.ResourceValueRecord;
import su.svn.api.models.dto.UpdateValueRecord;
import su.svn.api.repository.client.rest.ValueRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValueRecordRepositoryTest {

    @Mock
    @RestClient
    ValueRecordClient client;

    @Mock
    SecurityContextPrincipalHelper principalHelper;

    @InjectMocks
    ValueRecordRepository repository;

    @BeforeEach
    void setup() {
        MDC.put("requestId", "test-request");
    }

    @Test
    void shouldDeleteRecord() {
        UUID id = UUID.randomUUID();

        when(principalHelper.authorization()).thenReturn("Bearer token");
        when(client.delete(anyString(), anyString(), eq(id)))
                .thenReturn(Uni.createFrom().voidItem());

        repository.delete(id)
                .await().indefinitely();

        verify(client).delete(anyString(), anyString(), eq(id));
    }

    @Test
    void shouldPostRecord() {
        var dto = mock(NewValueRecord.class);
        var resource = mock(ResourceValueRecord.class);

        when(principalHelper.authorization()).thenReturn("Bearer token");
        when(client.post(anyString(), anyString(), eq(dto)))
                .thenReturn(Uni.createFrom().item(resource));

        var result = repository.post(dto)
                .await().indefinitely();

        assertNotNull(result);

        verify(client).post(anyString(), anyString(), eq(dto));
    }

    @Test
    void shouldPutRecord() {
        var dto = mock(UpdateValueRecord.class);
        var resource = mock(ResourceValueRecord.class);

        when(principalHelper.authorization()).thenReturn("Bearer token");
        when(client.put(anyString(), anyString(), eq(dto)))
                .thenReturn(Uni.createFrom().item(resource));

        var result = repository.put(dto)
                .await().indefinitely();

        assertNotNull(result);

        verify(client).put(anyString(), anyString(), eq(dto));
    }
}