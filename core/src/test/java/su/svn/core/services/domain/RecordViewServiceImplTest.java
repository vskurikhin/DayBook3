package su.svn.core.services.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import su.svn.core.domain.entities.RecordView;
import su.svn.lib.models.dto.ResourceRecordView;
import su.svn.core.models.dto.ResourceRecordViewFilter;
import su.svn.core.repository.RecordViewRepository;
import su.svn.core.repository.specifications.RecordViewSpecificationBuilder;
import su.svn.core.services.mappers.RecordViewMapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;



@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class RecordViewServiceImplTest {

    @Mock
    RecordViewRepository recordViewRepository;

    @Mock
    RecordViewSpecificationBuilder specificationBuilder;

    @Mock
    RecordViewMapper recordViewMapper;

    @InjectMocks
    RecordViewServiceImpl service;


    @Test
    void shouldReturnRecordById() {

        UUID id = UUID.randomUUID();

        RecordView entity = RecordView.builder()
                .build();

        ResourceRecordView resource =
                ResourceRecordView.builder()
                        .id(id)
                        .build();


        when(recordViewRepository.findById(id))
                .thenReturn(Optional.of(entity));

        when(recordViewMapper.toResource(entity))
                .thenReturn(resource);


        Optional<ResourceRecordView> result =
                service.getRecord(id);


        assertThat(result)
                .isPresent();

        assertThat(result.get())
                .isEqualTo(resource);


        verify(recordViewRepository)
                .findById(id);

        verify(recordViewMapper)
                .toResource(entity);
    }


    @Test
    void shouldReturnEmptyWhenRecordNotFound() {

        UUID id = UUID.randomUUID();


        when(recordViewRepository.findById(id))
                .thenReturn(Optional.empty());


        Optional<ResourceRecordView> result =
                service.getRecord(id);


        assertThat(result)
                .isEmpty();


        verify(recordViewRepository)
                .findById(id);

        verify(recordViewMapper, never())
                .toResource(any());
    }



    @Test
    void shouldReturnFilteredRecords() {

        ResourceRecordViewFilter filter =
                new ResourceRecordViewFilter(null, LocalDateTime.MIN, OffsetDateTime.MIN, OffsetDateTime.MAX, true);


        RecordView entity =
                RecordView.builder()
                        .build();


        ResourceRecordView resource =
                ResourceRecordView.builder()
                        .build();


        Page<RecordView> entityPage =
                new PageImpl<>(
                        List.of(entity)
                );


        when(specificationBuilder.build(filter))
                .thenReturn(null);


        when(recordViewRepository.findAll(
                nullable(Specification.class),
                any(Pageable.class)
        ))
                .thenReturn(entityPage);


        when(recordViewMapper.toResource(entity))
                .thenReturn(resource);



        Page<ResourceRecordView> result =
                service.getFilteredRecords(
                        filter,
                        PageRequest.of(0,10)
                );



        assertThat(result)
                .isNotEmpty();

        assertThat(result.getContent())
                .containsExactly(resource);



        verify(specificationBuilder)
                .build(filter);


        verify(recordViewRepository)
                .findAll(
                        nullable(Specification.class),
                        any(Pageable.class)
                );


        verify(recordViewMapper)
                .toResource(entity);
    }



    @Test
    void shouldReturnEmptyPageWhenNoRecordsFound() {

        ResourceRecordViewFilter filter =
                new ResourceRecordViewFilter(null, LocalDateTime.MIN, OffsetDateTime.MIN, OffsetDateTime.MAX, true);


        Page<RecordView> emptyPage =
                Page.empty();


        when(specificationBuilder.build(filter))
                .thenReturn(null);


        when(recordViewRepository.findAll(
                nullable(Specification.class),
                any(Pageable.class)
        ))
                .thenReturn(emptyPage);



        Page<ResourceRecordView> result =
                service.getFilteredRecords(
                        filter,
                        PageRequest.of(0,10)
                );



        assertThat(result)
                .isEmpty();



        verify(recordViewMapper, never())
                .toResource(any());
    }
}