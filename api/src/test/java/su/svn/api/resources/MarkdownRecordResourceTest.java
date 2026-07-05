package su.svn.api.resources;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.models.dto.NewMarkdownRecord;
import su.svn.api.models.dto.ResourceMarkdownRecord;
import su.svn.api.models.dto.UpdateMarkdownRecord;
import su.svn.api.services.domain.MarkdownRecordDataService;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarkdownRecordResourceTest {

    @Mock
    MarkdownRecordDataService service;

    @InjectMocks
    MarkdownRecordResource resource;

    UUID id;
    ResourceMarkdownRecord resourceRecord;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        resourceRecord = ResourceMarkdownRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("title")
                .markdown("# markdown")
                .userName("root")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(1)
                .build();
    }

    @Test
    void shouldCreateMarkdownRecord() {
        NewMarkdownRecord request = NewMarkdownRecord.builder()
                .parentId(UUID.randomUUID())
                .title("title")
                .markdown("# markdown")
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(1)
                .tags(Set.of("java"))
                .build();

        when(service.post(request))
                .thenReturn(Uni.createFrom().item(resourceRecord));

        try (var response = resource.create(request).await().indefinitely()) {
            assertNotNull(response);
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertEquals(resourceRecord, response.getEntity());
        }

        verify(service).post(request);
    }

    @Test
    void shouldDeleteMarkdownRecord() {
        when(service.delete(id))
                .thenReturn(Uni.createFrom().voidItem());

        Response response = resource.delete(id)
                .await()
                .indefinitely();

        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        verify(service).delete(id);
    }

    @Test
    void shouldUpdateMarkdownRecord() {
        UpdateMarkdownRecord request = UpdateMarkdownRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("updated")
                .markdown("## updated")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(2)
                .tags(Set.of("quarkus"))
                .build();

        when(service.put(request))
                .thenReturn(Uni.createFrom().item(resourceRecord));

        try (var response = resource.update(request).await().indefinitely()) {
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertEquals(resourceRecord, response.getEntity());
        }

        verify(service).put(request);
    }
}