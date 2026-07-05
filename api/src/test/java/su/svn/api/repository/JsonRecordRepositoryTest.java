package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import su.svn.api.models.dto.NewJsonRecord;
import su.svn.api.models.dto.ResourceJsonRecord;
import su.svn.api.models.dto.UpdateJsonRecord;
import su.svn.api.repository.client.rest.JsonRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static su.svn.lib.Constants.REQUEST_ID;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class JsonRecordRepositoryTest {

    private static final String AUTHORIZATION = "Bearer token";
    private static final String REQUEST_ID_VALUE = "req-123";

    @Mock
    JsonRecordClient jsonRecordClient;

    @Mock
    SecurityContextPrincipalHelper principalHelper;

    @InjectMocks
    JsonRecordRepository jsonRecordRepository;

    UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        MDC.put(REQUEST_ID, REQUEST_ID_VALUE);

        when(principalHelper.authorization())
                .thenReturn(AUTHORIZATION);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldDeleteRecord() {

        when(jsonRecordClient.delete(AUTHORIZATION, REQUEST_ID_VALUE, id))
                .thenReturn(Uni.createFrom().voidItem());

        Void result = jsonRecordRepository.delete(id)
                .await().indefinitely();

        assertThat(result).isNull();

        verify(principalHelper).authorization();
        verify(jsonRecordClient).delete(AUTHORIZATION, REQUEST_ID_VALUE, id);
    }

    @Test
    void shouldPostRecord() {

        NewJsonRecord request = NewJsonRecord.builder()
                .parentId(UUID.randomUUID())
                .title("title")
                .json(Map.of("key", "value"))
                .postAt(OffsetDateTime.now())
                .build();

        ResourceJsonRecord expected = ResourceJsonRecord.builder()
                .id(id)
                .title("title")
                .json(Map.of("key", "value"))
                .build();

        when(jsonRecordClient.post(AUTHORIZATION, REQUEST_ID_VALUE, request))
                .thenReturn(Uni.createFrom().item(expected));

        ResourceJsonRecord result = jsonRecordRepository.post(request)
                .await().indefinitely();

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.title()).isEqualTo("title");

        verify(principalHelper).authorization();
        verify(jsonRecordClient).post(AUTHORIZATION, REQUEST_ID_VALUE, request);
    }

    @Test
    void shouldPutRecord() {

        UpdateJsonRecord request = UpdateJsonRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("updated")
                .refreshAt(OffsetDateTime.now())
                .build();

        ResourceJsonRecord expected = ResourceJsonRecord.builder()
                .id(id)
                .title("updated")
                .build();

        when(jsonRecordClient.put(AUTHORIZATION, REQUEST_ID_VALUE, request))
                .thenReturn(Uni.createFrom().item(expected));

        ResourceJsonRecord result = jsonRecordRepository.put(request)
                .await().indefinitely();

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.title()).isEqualTo("updated");

        verify(principalHelper).authorization();
        verify(jsonRecordClient).put(AUTHORIZATION, REQUEST_ID_VALUE, request);
    }

    @Test
    void shouldUseNoneRequestIdWhenMdcIsEmpty() {

        MDC.clear();

        when(jsonRecordClient.delete(
                AUTHORIZATION,
                JsonRecordRepository.NONE,
                id
        )).thenReturn(Uni.createFrom().voidItem());

        jsonRecordRepository.delete(id)
                .await().indefinitely();

        verify(jsonRecordClient).delete(
                AUTHORIZATION,
                JsonRecordRepository.NONE,
                id
        );
    }
}
