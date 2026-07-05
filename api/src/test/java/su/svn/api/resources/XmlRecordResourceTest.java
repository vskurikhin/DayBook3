package su.svn.api.resources;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.models.dto.NewXmlRecord;
import su.svn.api.models.dto.ResourceXmlRecord;
import su.svn.api.models.dto.UpdateXmlRecord;
import su.svn.api.services.domain.XmlRecordDataService;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XmlRecordResourceTest {

    @InjectMocks
    XmlRecordResource resource;

    @Mock
    XmlRecordDataService service;

    @Test
    void shouldCreateXmlRecord() {
        var request = NewXmlRecord.builder()
                .parentId(UUID.randomUUID())
                .title("xml")
                .xml("<root/>")
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(5)
                .build();

        var responseDto = ResourceXmlRecord.builder()
                .id(UUID.randomUUID())
                .title("xml")
                .xml("<root/>")
                .visible(true)
                .flags(5)
                .build();

        when(service.post(request))
                .thenReturn(Uni.createFrom().item(responseDto));

        try (var response = resource.create(request).await().indefinitely()) {
            assertThat(response.getStatus())
                    .isEqualTo(Response.Status.CREATED.getStatusCode());

            assertThat(response.getEntity())
                    .isEqualTo(responseDto);
        }

        verify(service).post(request);
    }

    @Test
    void shouldDeleteXmlRecord() {
        var id = UUID.randomUUID();

        when(service.delete(id))
                .thenReturn(Uni.createFrom().voidItem());

        try (var response = resource.delete(id).await().indefinitely()) {
            assertThat(response.getStatus())
                    .isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
        }

        verify(service).delete(id);
    }

    @Test
    void shouldUpdateXmlRecord() {
        var id = UUID.randomUUID();

        var request = UpdateXmlRecord.builder()
                .id(id)
                .title("updated")
                .xml("<updated/>")
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(9)
                .build();

        var responseDto = ResourceXmlRecord.builder()
                .id(id)
                .title("updated")
                .xml("<updated/>")
                .visible(true)
                .flags(9)
                .build();

        when(service.put(request))
                .thenReturn(Uni.createFrom().item(responseDto));

        try (var response = resource.update(request).await().indefinitely()) {
            assertThat(response.getStatus())
                    .isEqualTo(Response.Status.OK.getStatusCode());

            assertThat(response.getEntity())
                    .isEqualTo(responseDto);
        }

        verify(service).put(request);
    }
}