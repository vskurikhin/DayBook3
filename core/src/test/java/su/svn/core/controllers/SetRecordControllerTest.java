package su.svn.core.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import su.svn.core.models.dto.NewSetRecord;
import su.svn.core.models.dto.ResourceSetRecord;
import su.svn.core.models.dto.UpdateSetRecord;
import su.svn.core.services.domain.SetRecordService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetRecordControllerTest {

    @Mock
    private SetRecordService service;

    @InjectMocks
    private SetRecordController controller;

    @Test
    void shouldCreateRecord() {
        ResourceSetRecord response = ResourceSetRecord.builder()
                .id(UUID.randomUUID())
                .build();

        when(service.save(any())).thenReturn(response);

        ResponseEntity<ResourceSetRecord> result =
                controller.createSetRecord(mock(NewSetRecord.class));

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void shouldReadRecord() throws Exception {
        UUID id = UUID.randomUUID();

        ResourceSetRecord response = ResourceSetRecord.builder()
                .id(id)
                .build();

        when(service.findById(id)).thenReturn(response);

        ResponseEntity<ResourceSetRecord> result =
                controller.readSetRecord(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void shouldUpdateRecord() {
        ResourceSetRecord response = ResourceSetRecord.builder()
                .id(UUID.randomUUID())
                .build();

        when(service.update(any())).thenReturn(response);

        ResponseEntity<ResourceSetRecord> result =
                controller.updateSetRecord(mock(UpdateSetRecord.class));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void shouldDeleteRecord() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> result =
                controller.deleteSetRecord(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(service).disable(id);
    }
}