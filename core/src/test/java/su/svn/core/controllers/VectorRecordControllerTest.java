/*
 * This file was last modified at 2026.05.24 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecordControllerTest.java
 */

package su.svn.core.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import su.svn.core.models.dto.NewVectorRecord;
import su.svn.core.models.dto.ResourceVectorRecord;
import su.svn.core.models.dto.UpdateVectorRecord;
import su.svn.core.services.domain.VectorRecordService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VectorRecordControllerTest {

    @Mock
    VectorRecordService recordService;

    @InjectMocks
    VectorRecordController controller;

    UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
    }

    @Test
    void createVectorRecord_shouldReturnCreatedResponse() {

        NewVectorRecord newRecord = mock(NewVectorRecord.class);
        ResourceVectorRecord resourceRecord = mock(ResourceVectorRecord.class);

        when(recordService.save(newRecord)).thenReturn(resourceRecord);

        ResponseEntity<ResourceVectorRecord> response =
                controller.createVectorRecord(newRecord);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(resourceRecord, response.getBody());

        verify(recordService).save(newRecord);
    }

    @Test
    void readVectorRecord_shouldReturnOkResponse() {

        ResourceVectorRecord resourceRecord = mock(ResourceVectorRecord.class);

        when(recordService.findById(id)).thenReturn(resourceRecord);

        ResponseEntity<ResourceVectorRecord> response =
                controller.readVectorRecord(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resourceRecord, response.getBody());

        verify(recordService).findById(id);
    }

    @Test
    void updateVectorRecord_shouldReturnOkResponse() {

        UpdateVectorRecord updateRecord = mock(UpdateVectorRecord.class);
        ResourceVectorRecord resourceRecord = mock(ResourceVectorRecord.class);

        when(recordService.update(updateRecord)).thenReturn(resourceRecord);

        ResponseEntity<ResourceVectorRecord> response =
                controller.updateVectorRecord(updateRecord);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resourceRecord, response.getBody());

        verify(recordService).update(updateRecord);
    }

    @Test
    void deleteVectorRecord_shouldReturnNoContentResponse() {

        doNothing().when(recordService).disable(id);

        ResponseEntity<Void> response =
                controller.deleteVectorRecord(id);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(recordService).disable(id);
    }
}