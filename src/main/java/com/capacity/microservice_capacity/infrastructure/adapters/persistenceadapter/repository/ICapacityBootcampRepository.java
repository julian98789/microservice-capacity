package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.repository;

import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.entity.CapacityBootcampEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


import java.util.Collection;

@Repository
public interface ICapacityBootcampRepository extends ReactiveCrudRepository<CapacityBootcampEntity, Long> {

    Flux<CapacityBootcampEntity> findByCapacityIdIn(Collection<Long> capacityIds);

    Flux<CapacityBootcampEntity> findByBootcampId(Long bootcampId);

    Flux<CapacityBootcampEntity> findAll();
}