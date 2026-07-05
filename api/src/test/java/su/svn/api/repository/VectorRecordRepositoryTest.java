package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.models.dto.NewVectorRecord;
import su.svn.api.models.dto.ResourceVectorRecord;
import su.svn.api.models.dto.UpdateVectorRecord;
import su.svn.api.repository.client.rest.VectorRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static su.svn.lib.Constants.REQUEST_ID;

@ExtendWith(MockitoExtension.class)
class VectorRecordRepositoryTest {

    @InjectMocks
    VectorRecordRepository repository;

    @Mock
    VectorRecordClient client;

    @Mock
    SecurityContextPrincipalHelper principalHelper;

    @BeforeEach
    void setup() {
        when(principalHelper.authorization())
                .thenReturn("Bearer token");
    }

    @AfterEach
    void cleanup() {
        MDC.clear();
    }

    @Test
    void shouldDelete() {
        var id = UUID.randomUUID();

        MDC.put(REQUEST_ID, "req-1");

        when(client.delete("Bearer token", "req-1", id))
                .thenReturn(Uni.createFrom().voidItem());

        repository.delete(id).await().indefinitely();

        verify(client).delete("Bearer token", "req-1", id);
    }

    @Test
    void shouldPost() {
        MDC.put(REQUEST_ID, "req-post");

        var request = NewVectorRecord.builder()
                .vector(new float[]{1.0f})
                .postAt(OffsetDateTime.now())
                .build();

        var response = ResourceVectorRecord.builder()
                .id(UUID.randomUUID())
                .vector(new float[]{1.0f})
                .build();

        when(client.post("Bearer token", "req-post", request))
                .thenReturn(Uni.createFrom().item(response));

        var result = repository.post(request)
                .await().indefinitely();

        assertThat(result).isEqualTo(response);

        verify(client).post("Bearer token", "req-post", request);
    }

    @Test
    void shouldPut() {
        MDC.put(REQUEST_ID, "req-put");

        var request = UpdateVectorRecord.builder()
                .id(UUID.randomUUID())
                .vector(new float[]{2.0f})
                .refreshAt(OffsetDateTime.now())
                .build();

        var response = ResourceVectorRecord.builder()
                .id(request.id())
                .vector(new float[]{2.0f})
                .build();

        when(client.put("Bearer token", "req-put", request))
                .thenReturn(Uni.createFrom().item(response));

        var result = repository.put(request)
                .await().indefinitely();

        assertThat(result).isEqualTo(response);

        verify(client).put("Bearer token", "req-put", request);
    }
}