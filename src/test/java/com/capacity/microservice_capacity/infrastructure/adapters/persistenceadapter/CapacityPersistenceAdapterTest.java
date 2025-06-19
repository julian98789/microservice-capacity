package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter;

import static org.junit.jupiter.api.Assertions.*;

import com.capacity.microservice_capacity.domain.model.Capacity;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.entity.CapacityEntity;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.mapper.ICapacityEntityMapper;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.repository.ICapacityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapacityPersistenceAdapterTest {

    @Mock
    private ICapacityRepository capacityRepository;
    @Mock
    private ICapacityEntityMapper entityMapper;

    private CapacityPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CapacityPersistenceAdapter(capacityRepository, entityMapper);
    }

    @Test
    void save_shouldReturnSavedModel() {
        Capacity model = new Capacity(1L, "Test", "Desc");
        CapacityEntity entity = new CapacityEntity();
        when(entityMapper.toEntity(model)).thenReturn(entity);
        when(capacityRepository.save(entity)).thenReturn(Mono.just(entity));
        when(entityMapper.toModel(entity)).thenReturn(model);

        StepVerifier.create(adapter.save(model))
                .expectNext(model)
                .verifyComplete();

        verify(capacityRepository).save(entity);
    }

    @Test
    void existsByName_shouldReturnTrueIfExists() {
        String name = "Test";
        CapacityEntity entity = new CapacityEntity();
        Capacity model = new Capacity(1L, name, "Desc");
        when(capacityRepository.findByName(name)).thenReturn(Mono.just(entity));
        when(entityMapper.toModel(entity)).thenReturn(model);

        StepVerifier.create(adapter.existsByName(name))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByName_shouldReturnFalseIfNotExists() {
        String name = "Test";
        when(capacityRepository.findByName(name)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.existsByName(name))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void deleteById_shouldCallRepository() {
        Long id = 1L;
        when(capacityRepository.deleteById(id)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(id))
                .verifyComplete();

        verify(capacityRepository).deleteById(id);
    }

    @Test
    void existsById_shouldReturnRepositoryValue() {
        Long id = 1L;
        when(capacityRepository.existsById(id)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsById(id))
                .expectNext(true)
                .verifyComplete();
    }
}