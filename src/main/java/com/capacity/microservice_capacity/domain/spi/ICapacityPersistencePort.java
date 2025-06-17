package com.capacity.microservice_capacity.domain.spi;

import com.capacity.microservice_capacity.domain.model.Capacity;
import reactor.core.publisher.Mono;

public interface ICapacityPersistencePort {
    Mono<Capacity> save(Capacity technology);
    Mono<Boolean> existsByName(String name);
    Mono<Void> deleteById(Long id);
    Mono<Boolean> existsById(Long id);
}
