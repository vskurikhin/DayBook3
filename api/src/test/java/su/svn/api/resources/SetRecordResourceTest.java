package su.svn.api.resources;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.models.dto.NewSetRecord;
import su.svn.api.models.dto.ResourceSetRecord;
import su.svn.api.models.dto.UpdateSetRecord;
import su.svn.api.services.domain.SetRecordDataService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetRecordResourceTest {

    @Mock
    SetRecordDataService service;

    @InjectMocks
    SetRecordResource resource;

    @Test
    void shouldCreateRecord() {
        var entry = mock(NewSetRecord.class);
        var responseRecord = mock(ResourceSetRecord.class);

        when(service.post(entry))
                .thenReturn(Uni.createFrom().item(responseRecord));

        try (var response = resource.create(entry).await().indefinitely()) {
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        }

        verify(service).post(entry);
    }

    @Test
    void shouldDeleteRecord() {
        UUID id = UUID.randomUUID();

        when(service.delete(id))
                .thenReturn(Uni.createFrom().voidItem());

        try (Response response = resource.delete(id).await().indefinitely()) {
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }

        verify(service).delete(id);
    }

    @Test
    void shouldUpdateRecord() {
        var entry = mock(UpdateSetRecord.class);
        var responseRecord = mock(ResourceSetRecord.class);

        when(service.put(entry))
                .thenReturn(Uni.createFrom().item(responseRecord));

        try (var response = resource.update(entry).await().indefinitely()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }

        verify(service).put(entry);
    }
}