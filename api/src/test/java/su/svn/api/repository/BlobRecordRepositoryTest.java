package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import su.svn.api.models.dto.NewBlobRecord;
import su.svn.api.models.dto.ResourceBlobRecord;
import su.svn.api.models.dto.UpdateBlobRecord;
import su.svn.api.repository.client.rest.BlobRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static su.svn.lib.Constants.REQUEST_ID;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class BlobRecordRepositoryTest {

    @InjectMocks
    BlobRecordRepository repository;

    @Mock
    BlobRecordClient client;

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
    void shouldDeleteRecord() {
        var id = UUID.randomUUID();

        MDC.put(REQUEST_ID, "req-1");

        when(client.delete("Bearer token", "req-1", id))
                .thenReturn(Uni.createFrom().voidItem());

        repository.delete(id)
                .await().indefinitely();

        verify(client).delete("Bearer token", "req-1", id);
    }

    @Test
    void shouldUseNoneWhenRequestIdMissing() {
        var id = UUID.randomUUID();

        when(client.delete("Bearer token", "NONE", id))
                .thenReturn(Uni.createFrom().voidItem());

        repository.delete(id)
                .await().indefinitely();

        verify(client).delete("Bearer token", "NONE", id);
    }

    @Test
    void shouldPostRecord() {
        MDC.put(REQUEST_ID, "req-post");

        var request = NewBlobRecord.builder()
                .title("test")
                .postAt(OffsetDateTime.now())
                .build();

        var response = ResourceBlobRecord.builder()
                .id(UUID.randomUUID())
                .title("test")
                .build();

        when(client.post("Bearer token", "req-post", request))
                .thenReturn(Uni.createFrom().item(response));

        var result = repository.post(request)
                .await().indefinitely();

        assertThat(result).isEqualTo(response);

        verify(client).post("Bearer token", "req-post", request);
    }

    @Test
    void shouldPutRecord() {
        MDC.put(REQUEST_ID, "req-put");

        var request = UpdateBlobRecord.builder()
                .id(UUID.randomUUID())
                .refreshAt(OffsetDateTime.now())
                .build();

        var response = ResourceBlobRecord.builder()
                .id(request.id())
                .build();

        when(client.put("Bearer token", "req-put", request))
                .thenReturn(Uni.createFrom().item(response));

        var result = repository.put(request)
                .await().indefinitely();

        assertThat(result.id()).isEqualTo(request.id());

        verify(client).put("Bearer token", "req-put", request);
    }
}