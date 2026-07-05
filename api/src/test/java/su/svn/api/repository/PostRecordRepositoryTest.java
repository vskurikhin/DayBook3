package su.svn.api.repository;

import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import su.svn.api.domain.entities.PostRecord;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class PostRecordRepositoryTest {

    private PostRecordRepository repository;

    @Mock
    Mutiny.SessionFactory sessionFactory;

    @Mock
    Mutiny.Session session;

    @BeforeEach
    void setUp() {

        //noinspection resource
        MockitoAnnotations.openMocks(this);

        repository = new PostRecordRepository();

        repository.mutinySessionFactory = sessionFactory;
    }


    @Test
    void shouldPersistAllRecords() {

        PostRecord record =
                PostRecord.builder()
                        .id(UUID.randomUUID())
                        .build();


        List<PostRecord> records =
                List.of(record);


        when(sessionFactory.withSession(any()))
                .thenAnswer(invocation -> {

                    java.util.function.Function<Mutiny.Session, Uni<Void>> function =
                            invocation.getArgument(0);

                    return function.apply(session);

                });


        when(session.setBatchSize(PostRecordRepository.BATCH_SIZE))
                .thenReturn(session);


        when(session.persistAll(any(Object[].class)))
                .thenReturn(Uni.createFrom().voidItem());


        List<PostRecord> result =
                repository.persistAll(records)
                        .await()
                        .indefinitely();


        assertThat(result)
                .containsExactly(record);


        verify(sessionFactory)
                .withSession(any());


        verify(session)
                .setBatchSize(PostRecordRepository.BATCH_SIZE);


        verify(session)
                .persistAll(any(Object[].class));
    }

    @Test
    void shouldUseBatchSizeWhenPersisting() {

        List<PostRecord> records =
                List.of(
                        PostRecord.builder()
                                .id(UUID.randomUUID())
                                .build()
                );


        when(sessionFactory.withSession(any()))
                .thenAnswer(invocation -> {

                    java.util.function.Function<Mutiny.Session, Uni<Void>> function =
                            invocation.getArgument(0);

                    return function.apply(session);

                });


        when(session.setBatchSize(anyInt()))
                .thenReturn(session);


        when(session.persistAll(any(Object[].class)))
                .thenReturn(Uni.createFrom().voidItem());



        repository.persistAll(records)
                .await()
                .indefinitely();



        verify(session)
                .setBatchSize(PostRecordRepository.BATCH_SIZE);
    }

    @Test
    void shouldReturnSameRecordsAfterPersist() {

        PostRecord record =
                PostRecord.builder()
                        .id(UUID.randomUUID())
                        .build();


        List<PostRecord> records =
                List.of(record);


        when(sessionFactory.withSession(any()))
                .thenAnswer(invocation -> {

                    java.util.function.Function<Mutiny.Session, Uni<Void>> function =
                            invocation.getArgument(0);

                    return function.apply(session);

                });


        when(session.setBatchSize(anyInt()))
                .thenReturn(session);


        when(session.persistAll(any(Object[].class)))
                .thenReturn(Uni.createFrom().voidItem());


        List<PostRecord> result =
                repository.persistAll(records)
                        .await()
                        .indefinitely();


        assertThat(result)
                .isSameAs(records);
    }
}