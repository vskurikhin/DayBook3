package su.svn.core.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import su.svn.core.models.dto.NewValueRecord;
import su.svn.core.models.dto.ResourceValueRecord;
import su.svn.core.models.dto.UpdateValueRecord;
import su.svn.core.services.domain.ValueRecordService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValueRecordControllerTest {

    @Mock
    ValueRecordService recordService;

    @InjectMocks
    ValueRecordController controller;

    UUID id;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID();
    }

    @Test
    void shouldCreateValueRecord() {
        NewValueRecord request = NewValueRecord.builder()
                .value("test")
                .build();

        ResourceValueRecord response = ResourceValueRecord.builder()
                .id(id)
                .value("test")
                .build();

        when(recordService.save(request)).thenReturn(response);

        ResponseEntity<ResourceValueRecord> result =
                controller.createValueRecord(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(recordService).save(request);
    }

    @Test
    void shouldReadValueRecord() {
        ResourceValueRecord response = ResourceValueRecord.builder()
                .id(id)
                .value("value")
                .build();

        when(recordService.findById(id)).thenReturn(response);

        ResponseEntity<ResourceValueRecord> result =
                controller.readValueRecord(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(recordService).findById(id);
    }

    @Test
    void shouldUpdateValueRecord() {
        UpdateValueRecord request = UpdateValueRecord.builder()
                .id(id)
                .value("updated")
                .build();

        ResourceValueRecord response = ResourceValueRecord.builder()
                .id(id)
                .value("updated")
                .build();

        when(recordService.update(request)).thenReturn(response);

        ResponseEntity<ResourceValueRecord> result =
                controller.updateValueRecord(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(recordService).update(request);
    }

    @Test
    void shouldDeleteValueRecord() {
        ResponseEntity<Void> result =
                controller.deleteValueRecord(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());

        verify(recordService).disable(id);
    }
}