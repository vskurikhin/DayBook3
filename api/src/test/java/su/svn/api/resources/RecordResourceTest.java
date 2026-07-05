package su.svn.api.resources;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.Page;
import su.svn.api.models.dto.RecordData;
import su.svn.api.models.dto.RecordDataPage;
import su.svn.api.services.domain.PostRecordDataSyncService;
import su.svn.api.services.mappers.PageRecordDataMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RecordResourceTest {


    @Mock
    PostRecordDataSyncService service;


    @Mock
    PageRecordDataMapper mapper;


    @InjectMocks
    RecordResource resource;


    UUID id;

    PostRecord postRecord;

    RecordData recordData;


    @BeforeEach
    void setUp() {

        id = UUID.randomUUID();


        postRecord = PostRecord.builder()
                .id(id)
                .title("test record")
                .build();


        recordData = RecordData.builder()
                .id(id)
                .title("test record")
                .build();
    }



    @Test
    void shouldReturnRecordById() {


        when(service.readPostRecord(id))
                .thenReturn(Uni.createFrom()
                        .item(postRecord));


        when(mapper.toDto(postRecord))
                .thenReturn(recordData);



        RecordData result =
                resource.record(id)
                        .await()
                        .indefinitely();



        assertNotNull(result);

        assertEquals(
                recordData,
                result
        );


        verify(service)
                .readPostRecord(id);


        verify(mapper)
                .toDto(postRecord);
    }



    @Test
    void shouldReturnPageOfRecords() {


        Page<PostRecord> page =
                new Page<>(
                        List.of(postRecord),
                        1,
                        0,
                        1,
                        1
                );


        RecordDataPage response =
                new RecordDataPage(
                        List.of(),
                        0,
                        0,
                        10,
                        0
                );



        when(service.readPage(0, (byte) 10))
                .thenReturn(
                        Uni.createFrom()
                                .item(page)
                );


        when(mapper.toPage(page))
                .thenReturn(response);



        RecordDataPage result =
                resource.page(0, (byte) 10)
                        .await()
                        .indefinitely();



        assertNotNull(result);

        assertEquals(
                response,
                result
        );


        verify(service)
                .readPage(0, (byte) 10);


        verify(mapper)
                .toPage(page);
    }



    @Test
    void shouldHandleEmptyRecordResult() {


        when(service.readPostRecord(id))
                .thenReturn(
                        Uni.createFrom()
                                .nullItem()
                );


        RecordData result =
                resource.record(id)
                        .await()
                        .indefinitely();



        assertNull(result);


        verify(service)
                .readPostRecord(id);
    }



    @Test
    void shouldReturnEmptyPage() {


        Page<PostRecord> emptyPage =
                new Page<>(
                        List.of(),
                        0,
                        0,
                        0,
                        0
                );


        RecordDataPage response =
                new RecordDataPage(
                        List.of(),
                        0,
                        0,
                        10,
                        0
                );



        when(service.readPage(0, (byte) 10))
                .thenReturn(
                        Uni.createFrom()
                                .item(emptyPage)
                );


        when(mapper.toPage(emptyPage))
                .thenReturn(response);



        RecordDataPage result =
                resource.page(0, (byte) 10)
                        .await()
                        .indefinitely();



        assertNotNull(result);


        verify(service)
                .readPage(0, (byte) 10);


        verify(mapper)
                .toPage(emptyPage);
    }
}