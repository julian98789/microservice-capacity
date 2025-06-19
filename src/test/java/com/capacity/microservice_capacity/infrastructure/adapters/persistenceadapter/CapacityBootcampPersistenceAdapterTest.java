package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter;

import com.capacity.microservice_capacity.domain.model.CapacityBootcamp;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.entity.CapacityBootcampEntity;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.mapper.ICapacityBootcampEntityMapper;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.repository.ICapacityBootcampRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapacityBootcampPersistenceAdapterTest {

    @Mock
    private ICapacityBootcampRepository repository;
    @Mock
    private ICapacityBootcampEntityMapper mapper;

    private CapacityBootcampPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CapacityBootcampPersistenceAdapter(repository, mapper);
    }

    @Test
    void saveAll_shouldSaveEntities() {
        CapacityBootcamp model = new CapacityBootcamp(1L, 2L, 3L);
        CapacityBootcampEntity entity = new CapacityBootcampEntity();
        when(mapper.toEntity(model)).thenReturn(entity);
        when(repository.saveAll(anyList())).thenReturn(Flux.just(entity));

        StepVerifier.create(adapter.saveAll(List.of(model)))
                .verifyComplete();

        verify(repository).saveAll(anyList());
    }

    @Test
    void findByCapacityIds_shouldReturnMappedModels() {
        CapacityBootcampEntity entity = new CapacityBootcampEntity();
        entity.setId(1L);
        entity.setCapacityId(2L);
        entity.setBootcampId(3L);
        CapacityBootcamp model = new CapacityBootcamp(1L, 2L, 3L);

        when(repository.findByCapacityIdIn(List.of(2L))).thenReturn(Flux.just(entity));
        when(mapper.toModel(entity)).thenReturn(model);

        StepVerifier.create(adapter.findByCapacityIds(List.of(2L)))
                .expectNext(model)
                .verifyComplete();
    }

    @Test
    void findByBootcampId_shouldReturnMappedModels() {
        CapacityBootcampEntity entity = new CapacityBootcampEntity();
        entity.setId(1L);
        entity.setCapacityId(2L);
        entity.setBootcampId(3L);
        CapacityBootcamp model = new CapacityBootcamp(1L, 2L, 3L);

        when(repository.findByBootcampId(3L)).thenReturn(Flux.just(entity));
        when(mapper.toModel(entity)).thenReturn(model);

        StepVerifier.create(adapter.findByBootcampId(3L))
                .expectNext(model)
                .verifyComplete();
    }

    @Test
    void getAllBootcampRelationCounts_shouldReturnCounts() {
        CapacityBootcampEntity entity1 = new CapacityBootcampEntity();
        entity1.setBootcampId(10L);
        CapacityBootcampEntity entity2 = new CapacityBootcampEntity();
        entity2.setBootcampId(10L);

        when(repository.findAll()).thenReturn(Flux.just(entity1, entity2));

        StepVerifier.create(adapter.getAllBootcampRelationCounts())
                .expectNextMatches(count -> count.bootcampId().equals(10L) && count.relationCount() == 2)
                .verifyComplete();
    }

    @Test
    void deleteByCapacityIdAndBootcampId_shouldCallRepository() {
        when(repository.deleteByCapacityIdAndBootcampId(2L, 3L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteByCapacityIdAndBootcampId(2L, 3L))
                .verifyComplete();

        verify(repository).deleteByCapacityIdAndBootcampId(2L, 3L);
    }
}