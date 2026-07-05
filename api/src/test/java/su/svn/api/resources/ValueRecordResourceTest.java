package su.svn.api.resources;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.models.dto.NewValueRecord;
import su.svn.api.models.dto.ResourceValueRecord;
import su.svn.api.models.dto.UpdateValueRecord;
import su.svn.api.services.domain.ValueRecordDataService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValueRecordResourceTest {

    @Mock
    ValueRecordDataService service;

    @InjectMocks
    ValueRecordResource resource;

    @Test
    void shouldCreateRecord() {
        var entry = mock(NewValueRecord.class);
        var responseRecord = mock(ResourceValueRecord.class);

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
        var entry = mock(UpdateValueRecord.class);
        var responseRecord = mock(ResourceValueRecord.class);

        when(service.put(entry))
                .thenReturn(Uni.createFrom().item(responseRecord));

        try (var response = resource.update(entry).await().indefinitely()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }

        verify(service).put(entry);
    }
}