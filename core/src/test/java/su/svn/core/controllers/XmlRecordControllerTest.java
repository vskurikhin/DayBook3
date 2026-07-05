package su.svn.core.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import su.svn.core.models.dto.NewXmlRecord;
import su.svn.core.models.dto.ResourceXmlRecord;
import su.svn.core.models.dto.UpdateXmlRecord;
import su.svn.core.services.domain.XmlRecordService;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class XmlRecordControllerTest {

    @Mock
    private XmlRecordService recordService;

    @InjectMocks
    private XmlRecordController controller;

    private UUID id;
    private ResourceXmlRecord resourceRecord;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        resourceRecord = ResourceXmlRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("XML Title")
                .xml("<root/>")
                .userName("user")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(1)
                .tags(Set.of("xml"))
                .build();
    }

    @Test
    void shouldCreateXmlRecord() {
        NewXmlRecord request = NewXmlRecord.builder()
                .parentId(UUID.randomUUID())
                .title("XML Title")
                .xml("<root/>")
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(1)
                .tags(Set.of("xml"))
                .build();

        when(recordService.save(request)).thenReturn(resourceRecord);

        ResponseEntity<ResourceXmlRecord> response =
                controller.createXmlRecord(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(resourceRecord, response.getBody());

        verify(recordService).save(request);
    }

    @Test
    void shouldReadXmlRecord() {
        when(recordService.findById(id)).thenReturn(resourceRecord);

        ResponseEntity<ResourceXmlRecord> response =
                controller.readXmlRecord(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resourceRecord, response.getBody());

        verify(recordService).findById(id);
    }

    @Test
    void shouldUpdateXmlRecord() {
        UpdateXmlRecord request = UpdateXmlRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("Updated XML")
                .xml("<updated/>")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(2)
                .tags(Set.of("updated"))
                .build();

        when(recordService.update(request)).thenReturn(resourceRecord);

        ResponseEntity<ResourceXmlRecord> response =
                controller.updateXmlRecord(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resourceRecord, response.getBody());

        verify(recordService).update(request);
    }

    @Test
    void shouldDeleteXmlRecord() {
        doNothing().when(recordService).disable(id);

        ResponseEntity<Void> response =
                controller.deleteXmlRecord(id);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(recordService).disable(id);
    }
}