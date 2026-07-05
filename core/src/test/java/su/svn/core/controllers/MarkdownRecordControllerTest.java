package su.svn.core.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import su.svn.core.models.dto.NewMarkdownRecord;
import su.svn.core.models.dto.ResourceMarkdownRecord;
import su.svn.core.models.dto.UpdateMarkdownRecord;
import su.svn.core.services.domain.MarkdownRecordService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkdownRecordControllerTest {

    @Mock
    MarkdownRecordService recordService;

    @InjectMocks
    MarkdownRecordController controller;

    UUID id;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID();
    }

    @Test
    void shouldCreateMarkdownRecord() {
        NewMarkdownRecord request = NewMarkdownRecord.builder()
                .markdown("# test")
                .build();

        ResourceMarkdownRecord response = ResourceMarkdownRecord.builder()
                .id(id)
                .markdown("# test")
                .build();

        when(recordService.save(request)).thenReturn(response);

        ResponseEntity<ResourceMarkdownRecord> result =
                controller.createMarkdownRecord(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(recordService).save(request);
    }

    @Test
    void shouldReadMarkdownRecord() {
        ResourceMarkdownRecord response = ResourceMarkdownRecord.builder()
                .id(id)
                .markdown("# markdown")
                .build();

        when(recordService.findById(id)).thenReturn(response);

        ResponseEntity<ResourceMarkdownRecord> result =
                controller.readMarkdownRecord(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(recordService).findById(id);
    }

    @Test
    void shouldUpdateMarkdownRecord() {
        UpdateMarkdownRecord request = UpdateMarkdownRecord.builder()
                .id(id)
                .markdown("updated")
                .build();

        ResourceMarkdownRecord response = ResourceMarkdownRecord.builder()
                .id(id)
                .markdown("updated")
                .build();

        when(recordService.update(request)).thenReturn(response);

        ResponseEntity<ResourceMarkdownRecord> result =
                controller.updateMarkdownRecord(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(recordService).update(request);
    }

    @Test
    void shouldDeleteMarkdownRecord() {
        ResponseEntity<Void> result =
                controller.deleteMarkdownRecord(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());

        verify(recordService).disable(id);
    }
}