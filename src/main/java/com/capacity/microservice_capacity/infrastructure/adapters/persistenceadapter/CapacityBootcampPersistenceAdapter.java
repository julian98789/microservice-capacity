package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter;

import com.capacity.microservice_capacity.domain.model.CapacityBootcamp;
import com.capacity.microservice_capacity.domain.spi.ICapacityBootcampPersistencePort;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.mapper.ICapacityBootcampEntityMapper;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.repository.ICapacityBootcampRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class CapacityBootcampPersistenceAdapter implements ICapacityBootcampPersistencePort {

    private final ICapacityBootcampRepository repository;
    private final ICapacityBootcampEntityMapper mapper;


    @Override
    public Mono<Void> saveAll(List<CapacityBootcamp> associations) {
        return Flux.fromIterable(associations)
                .map(mapper::toEntity)
                .collectList()
                .flatMapMany(repository::saveAll)
                .then();
    }

    @Override
    public Flux<CapacityBootcamp> findByCapacityIds(List<Long> capacityIds) {
        return repository.findByCapacityIdIn(capacityIds)
                .map(mapper::toModel);
    }

    @Override
    public Flux<CapacityBootcamp> findByBootcampId(Long bootcampId) {
        return repository.findByBootcampId(bootcampId)
                .map(mapper::toModel);
    }
}
