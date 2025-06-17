package com.capacity.microservice_capacity.domain.api;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ICapacityBootcampServicePort {
    Mono<Boolean> associateCapabilityToBootcamp(List<Long> capacityIds, Long bootcampId);
}
