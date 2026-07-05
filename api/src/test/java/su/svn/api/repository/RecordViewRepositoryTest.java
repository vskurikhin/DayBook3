package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.*;
import su.svn.api.repository.client.rest.RecordViewClient;
import su.svn.api.services.mappers.EntityModelResourceRecordMapper;
import su.svn.api.services.mappers.JsonRecordMapper;
import su.svn.api.services.security.SecurityContextPrincipalHelper;
import su.svn.lib.RecordType;
import su.svn.lib.models.dto.EntityModelResourceRecordView;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static su.svn.lib.Constants.REQUEST_ID;

@ExtendWith(MockitoExtension.class)
class RecordViewRepositoryTest {

    @Mock
    private EntityModelResourceRecordMapper resourceRecordMapper;

    @Mock
    private RecordViewClient recordViewClient;

    @Mock
    private SecurityContextPrincipalHelper principalHelper;

    @InjectMocks
    private RecordViewRepository repository;

    @BeforeEach
    void setUp() {
        MDC.put(REQUEST_ID, "request-id");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    // TODO @Test
    @SuppressWarnings("unused")
    void shouldReadPage() {
        // given
        int pageIndex = 0;
        byte size = 10;

        UUID id = UUID.randomUUID();

        EntityModelResourceRecordView entityModel =
                EntityModelResourceRecordView.builder()
                        .id(id)
                        .visible(true)
                        .flags(1)
                        .parentId(UUID.randomUUID())
                        .type(RecordType.Json)
                        .userName("test-user")
                        .postAt(OffsetDateTime.now())
                        .refreshAt(OffsetDateTime.now())
                        .lastChangedTime(LocalDateTime.now())
                        .title("test-title")
                        .json(Map.of("key", "value"))
                        .tags(List.of("tag1"))
                        .build();

        PagedModelEntityModelResourceRecordViewEmbedded embedded =
                PagedModelEntityModelResourceRecordViewEmbedded.builder()
                        .resourceRecordViewList(List.of(entityModel))
                        .build();

        PageMetadata metadata = PageMetadata.builder()
                .size(10L)
                .totalElements(100L)
                .totalPages(10L)
                .number(0L)
                .build();

        PagedModelEntityModelResourceRecordView pageModel = PagedModelEntityModelResourceRecordView.builder()
                .embedded(embedded)
                .page(metadata)
                .build();

        PostRecord postRecord = PostRecord.builder()
                .id(id)
                .title("test-title")
                .type(RecordType.Json)
                .build();

        when(principalHelper.authorization()).thenReturn("Bearer token");

        when(recordViewClient.getByPageIndexAndSizeAsUni(
                eq("Bearer token"),
                eq("request-id"),
                eq(pageIndex),
                eq(size),
                eq(RecordViewRepository.SORT_PAGE_PARAMS)
        )).thenReturn(Uni.createFrom().item(pageModel));

        when(resourceRecordMapper.toEntity(entityModel))
                .thenReturn(postRecord);

        // when
        Page<PostRecord> result = repository.readPage(pageIndex, size)
                .await().indefinitely();

        // then
        assertNotNull(result);
        assertEquals(1, result.list().size());
        assertEquals(postRecord, result.list().getFirst());

        assertEquals(10L, result.pageCount());
        assertEquals(0L, result.pageIndex());
        assertEquals(10L, result.pageSize());

        verify(principalHelper).authorization();

        verify(recordViewClient).getByPageIndexAndSizeAsUni(
                "Bearer token",
                "request-id",
                pageIndex,
                size,
                RecordViewRepository.SORT_PAGE_PARAMS
        );

        verify(resourceRecordMapper).toEntity(entityModel);
    }

    @Test
    void shouldReadList() {
        // given
        int pageIndex = 1;
        int size = 20;
        LocalDateTime fromTime = LocalDateTime.now();

        UUID id = UUID.randomUUID();

        EntityModelResourceRecordView entityModel =
                EntityModelResourceRecordView.builder()
                        .id(id)
                        .type(RecordType.Json)
                        .title("record-title")
                        .postAt(OffsetDateTime.now())
                        .lastChangedTime(LocalDateTime.now())
                        .build();

        PagedModelEntityModelResourceRecordViewEmbedded embedded =
                PagedModelEntityModelResourceRecordViewEmbedded.builder()
                        .resourceRecordViewList(List.of(entityModel))
                        .build();

        PagedModelEntityModelResourceRecordView pageModel = PagedModelEntityModelResourceRecordView.builder()
                .embedded(embedded)
                .page(PageMetadata.builder().build())
                .build();

        PostRecord postRecord = PostRecord.builder()
                .id(id)
                .title("record-title")
                .type(RecordType.Json)
                .build();

        when(principalHelper.authorization()).thenReturn("Bearer token");

        when(recordViewClient.getByPageIndexAndSizeAndFromTimeAsUni(
                eq("Bearer token"),
                eq("request-id"),
                eq(pageIndex),
                eq(size),
                eq(RecordViewRepository.SORT_LIST_PARAMS),
                eq(fromTime),
                eq(true)
        )).thenReturn(Uni.createFrom().item(pageModel));

        when(resourceRecordMapper.toEntity(entityModel))
                .thenReturn(postRecord);

        // when
        List<PostRecord> result = repository.readList(pageIndex, size, fromTime)
                .await().indefinitely();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(postRecord, result.getFirst());

        verify(principalHelper).authorization();

        verify(recordViewClient).getByPageIndexAndSizeAndFromTimeAsUni(
                "Bearer token",
                "request-id",
                pageIndex,
                size,
                RecordViewRepository.SORT_LIST_PARAMS,
                fromTime,
                true
        );

        verify(resourceRecordMapper).toEntity(entityModel);
    }

    @Test
    void shouldCreateEmbeddedWithDefaultConstructor() {
        // when
        PagedModelEntityModelResourceRecordViewEmbedded embedded =
                new PagedModelEntityModelResourceRecordViewEmbedded();

        // then
        assertNotNull(embedded.resourceRecordViewList());
        assertTrue(embedded.resourceRecordViewList().isEmpty());
    }

    @Test
    void shouldCreateEmbeddedWithNullList() {
        // when
        PagedModelEntityModelResourceRecordViewEmbedded embedded =
                PagedModelEntityModelResourceRecordViewEmbedded.builder()
                        .resourceRecordViewList(null)
                        .build();

        // then
        assertNotNull(embedded.resourceRecordViewList());
        assertTrue(embedded.resourceRecordViewList().isEmpty());
    }

    @Test
    void shouldCreateEntityModelWithDefaults() {
        // when
        EntityModelResourceRecordView entity =
                EntityModelResourceRecordView.builder()
                        .id(UUID.randomUUID())
                        .type(null)
                        .json(null)
                        .tags(null)
                        .links(null)
                        .build();

        // then
        assertEquals(RecordType.Base, entity.type());

        assertNull(entity.json());

        assertNull(entity.tags());

        assertNotNull(entity.links());
        assertTrue(entity.links().isEmpty());
    }

    @Test
    void shouldCreateEntityModelWithDefaultConstructor() {
        // when
        EntityModelResourceRecordView entity =
                new EntityModelResourceRecordView();

        // then
        assertNotNull(entity.id());

        assertEquals(RecordType.Base, entity.type());

        assertNull(entity.json());

        assertNull(entity.tags());

        assertNotNull(entity.links());
        assertTrue(entity.links().isEmpty());

        assertFalse(entity.visible());
        assertEquals(0, entity.flags());
    }

    @Test
    void shouldCreatePageMetadataWithDefaultConstructor() {
        // when
        PageMetadata metadata = new PageMetadata();

        // then
        assertEquals(Long.MIN_VALUE, metadata.size());
        assertEquals(Long.MIN_VALUE, metadata.totalElements());
        assertEquals(Long.MIN_VALUE, metadata.totalPages());
        assertEquals(Long.MIN_VALUE, metadata.number());
    }

    @Test
    void shouldCreatePageMetadataWithBuilder() {
        // when
        PageMetadata metadata = PageMetadata.builder()
                .size(10L)
                .totalElements(100L)
                .totalPages(5L)
                .number(1L)
                .build();

        // then
        assertEquals(10L, metadata.size());
        assertEquals(100L, metadata.totalElements());
        assertEquals(5L, metadata.totalPages());
        assertEquals(1L, metadata.number());
    }

    @Test
    void shouldReadRecord() {

        // given
        UUID id = UUID.randomUUID();


        EntityModelResourceRecordView entityModel =
                EntityModelResourceRecordView.builder()
                        .id(id)
                        .type(RecordType.Json)
                        .title("record-title")
                        .postAt(OffsetDateTime.now())
                        .lastChangedTime(LocalDateTime.now())
                        .json(Map.of("key", "value"))
                        .build();


        PostRecord postRecord =
                PostRecord.builder()
                        .id(id)
                        .title("record-title")
                        .type(RecordType.Json)
                        .build();



        when(principalHelper.authorization())
                .thenReturn("Bearer token");


        when(recordViewClient.getRecord(
                eq("Bearer token"),
                eq("request-id"),
                eq(id)
        ))
                .thenReturn(Uni.createFrom().item(entityModel));


        when(resourceRecordMapper.toEntity(entityModel))
                .thenReturn(postRecord);



        // when
        PostRecord result =
                repository.readRecord(id)
                        .await()
                        .indefinitely();



        // then
        assertNotNull(result);

        assertEquals(postRecord, result);

        assertEquals(id, result.id());

        assertEquals("record-title", result.title());



        verify(principalHelper)
                .authorization();


        verify(recordViewClient)
                .getRecord(
                        "Bearer token",
                        "request-id",
                        id
                );


        verify(resourceRecordMapper)
                .toEntity(entityModel);
    }

    @Test
    void shouldFailWhenRecordViewClientFails() {

        UUID id = UUID.randomUUID();


        when(principalHelper.authorization())
                .thenReturn("Bearer token");


        when(recordViewClient.getRecord(
                eq("Bearer token"),
                eq("request-id"),
                eq(id)
        ))
                .thenReturn(
                        Uni.createFrom()
                                .failure(new RuntimeException("client error"))
                );


        assertThrows(
                RuntimeException.class,
                () -> repository.readRecord(id)
                        .await()
                        .indefinitely()
        );


        verify(resourceRecordMapper, never())
                .toEntity(any());
    }
}