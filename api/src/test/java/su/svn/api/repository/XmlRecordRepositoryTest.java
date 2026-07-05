package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.models.dto.NewXmlRecord;
import su.svn.api.models.dto.ResourceXmlRecord;
import su.svn.api.models.dto.UpdateXmlRecord;
import su.svn.api.repository.client.rest.XmlRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static su.svn.lib.Constants.REQUEST_ID;

@ExtendWith(MockitoExtension.class)
class XmlRecordRepositoryTest {

    @InjectMocks
    XmlRecordRepository repository;

    @Mock
    XmlRecordClient client;

    @Mock
    SecurityContextPrincipalHelper principalHelper;

    @BeforeEach
    void setUp() {
        MDC.put(REQUEST_ID, "req-1");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldDeleteRecord() {
        var id = UUID.randomUUID();

        when(principalHelper.authorization())
                .thenReturn("Bearer token");

        when(client.delete("Bearer token", "req-1", id))
                .thenReturn(Uni.createFrom().voidItem());

        repository.delete(id)
                .await()
                .indefinitely();

        verify(client).delete("Bearer token", "req-1", id);
    }

    @Test
    void shouldPostRecord() {
        var request = NewXmlRecord.builder()
                .parentId(UUID.randomUUID())
                .title("xml")
                .xml("<root/>")
                .postAt(OffsetDateTime.now())
                .build();

        var response = ResourceXmlRecord.builder()
                .id(UUID.randomUUID())
                .xml("<root/>")
                .build();

        when(principalHelper.authorization())
                .thenReturn("Bearer token");

        when(client.post("Bearer token", "req-1", request))
                .thenReturn(Uni.createFrom().item(response));

        var result = repository.post(request)
                .await()
                .indefinitely();

        assertThat(result).isEqualTo(response);

        verify(client).post("Bearer token", "req-1", request);
    }

    @Test
    void shouldPutRecord() {
        var request = UpdateXmlRecord.builder()
                .id(UUID.randomUUID())
                .xml("<updated/>")
                .refreshAt(OffsetDateTime.now())
                .build();

        var response = ResourceXmlRecord.builder()
                .id(request.id())
                .xml("<updated/>")
                .build();

        when(principalHelper.authorization())
                .thenReturn("Bearer token");

        when(client.put("Bearer token", "req-1", request))
                .thenReturn(Uni.createFrom().item(response));

        var result = repository.put(request)
                .await()
                .indefinitely();

        assertThat(result).isEqualTo(response);

        verify(client).put("Bearer token", "req-1", request);
    }
}