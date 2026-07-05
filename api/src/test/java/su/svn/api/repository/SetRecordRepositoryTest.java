package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import su.svn.api.models.dto.NewSetRecord;
import su.svn.api.models.dto.ResourceSetRecord;
import su.svn.api.models.dto.UpdateSetRecord;
import su.svn.api.repository.client.rest.SetRecordClient;
import su.svn.api.services.security.SecurityContextPrincipalHelper;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static su.svn.lib.Constants.REQUEST_ID;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SetRecordRepositoryTest {

    private static final String AUTHORIZATION = "Bearer token";
    private static final String REQUEST_ID_VALUE = "req-123";

    @Mock
    SetRecordClient client;

    @Mock
    SecurityContextPrincipalHelper principalHelper;

    @InjectMocks
    SetRecordRepository repository;

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

        when(client.delete(AUTHORIZATION, REQUEST_ID_VALUE, id))
                .thenReturn(Uni.createFrom().voidItem());

        Void result = repository.delete(id)
                .await().indefinitely();

        assertThat(result).isNull();

        verify(principalHelper).authorization();
        verify(client).delete(AUTHORIZATION, REQUEST_ID_VALUE, id);
    }

    @Test
    void shouldPostRecord() {

        NewSetRecord request = NewSetRecord.builder()
                .parentId(UUID.randomUUID())
                .title("title")
                .texts(Set.of("value"))
                .postAt(OffsetDateTime.now())
                .build();

        ResourceSetRecord expected = ResourceSetRecord.builder()
                .id(id)
                .title("title")
                .texts(Set.of("value"))
                .build();

        when(client.post(AUTHORIZATION, REQUEST_ID_VALUE, request))
                .thenReturn(Uni.createFrom().item(expected));

        ResourceSetRecord result = repository.post(request)
                .await().indefinitely();

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.title()).isEqualTo("title");

        verify(principalHelper).authorization();
        verify(client).post(AUTHORIZATION, REQUEST_ID_VALUE, request);
    }

    @Test
    void shouldPutRecord() {

        UpdateSetRecord request = UpdateSetRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("updated")
                .refreshAt(OffsetDateTime.now())
                .build();

        ResourceSetRecord expected = ResourceSetRecord.builder()
                .id(id)
                .title("updated")
                .build();

        when(client.put(AUTHORIZATION, REQUEST_ID_VALUE, request))
                .thenReturn(Uni.createFrom().item(expected));

        ResourceSetRecord result = repository.put(request)
                .await().indefinitely();

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.title()).isEqualTo("updated");

        verify(principalHelper).authorization();
        verify(client).put(AUTHORIZATION, REQUEST_ID_VALUE, request);
    }

    @Test
    void shouldUseNoneRequestIdWhenMdcIsEmpty() {

        MDC.clear();

        when(client.delete(
                AUTHORIZATION,
                SetRecordRepository.NONE,
                id
        )).thenReturn(Uni.createFrom().voidItem());

        repository.delete(id)
                .await().indefinitely();

        verify(client).delete(
                AUTHORIZATION,
                SetRecordRepository.NONE,
                id
        );
    }
}
