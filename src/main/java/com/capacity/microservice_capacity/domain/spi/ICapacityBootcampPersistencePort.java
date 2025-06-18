package com.capacity.microservice_capacity.domain.spi;

import com.capacity.microservice_capacity.domain.model.CapacityBootcamp;
import com.capacity.microservice_capacity.domain.model.CapacityBootcampCount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ICapacityBootcampPersistencePort {

    Mono<Void> saveAll(List<CapacityBootcamp> associations);
    Flux<CapacityBootcamp> findByCapacityIds(List<Long> capacityIds);
    Flux<CapacityBootcamp> findByBootcampId(Long bootcampId);
    Flux<CapacityBootcampCount> getAllBootcampRelationCounts();
    Mono<Void> deleteByCapacityIdAndBootcampId(Long capacityId, Long bootcampId);


}
