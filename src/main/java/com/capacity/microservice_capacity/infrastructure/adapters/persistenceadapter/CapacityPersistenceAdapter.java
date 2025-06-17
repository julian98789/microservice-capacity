package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter;

import com.capacity.microservice_capacity.domain.model.Capacity;
import com.capacity.microservice_capacity.domain.spi.ICapacityPersistencePort;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.entity.CapacityEntity;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.mapper.ICapacityEntityMapper;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.repository.ICapacityRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class CapacityPersistenceAdapter implements ICapacityPersistencePort {
    private final ICapacityRepository capacityRepository;
    private final ICapacityEntityMapper technologyEntityMapper;

    @Override
    public Mono<Capacity> save(Capacity capacity) {
        CapacityEntity entity = technologyEntityMapper.toEntity(capacity);
        return capacityRepository.save(entity)
                .map(technologyEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return capacityRepository.findByName(name)
                .map(technologyEntityMapper::toModel)
                .map(tech -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return capacityRepository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return capacityRepository.existsById(id);
    }


}