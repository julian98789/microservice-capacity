package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter;

import com.capacity.microservice_capacity.domain.model.CapacityBootcamp;
import com.capacity.microservice_capacity.domain.model.CapacityBootcampCount;
import com.capacity.microservice_capacity.domain.spi.ICapacityBootcampPersistencePort;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.entity.CapacityBootcampEntity;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.mapper.ICapacityBootcampEntityMapper;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.repository.ICapacityBootcampRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    public Flux<CapacityBootcampCount> getAllBootcampRelationCounts() {
        return repository.findAll()
                .groupBy(CapacityBootcampEntity::getBootcampId)
                .flatMap(groupedFlux ->
                        groupedFlux.count()
                                .map(count -> new CapacityBootcampCount(groupedFlux.key(), count))
                );
    }

    @Override
    @Transactional
    public Mono<Void> deleteByCapacityIdAndBootcampId(Long capacityId, Long bootcampId) {
        return repository.deleteByCapacityIdAndBootcampId(capacityId, bootcampId);
    }
}
