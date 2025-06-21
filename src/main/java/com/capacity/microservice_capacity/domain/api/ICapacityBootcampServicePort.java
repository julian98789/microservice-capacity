package com.capacity.microservice_capacity.domain.api;

import com.capacity.microservice_capacity.domain.model.CapacityBootcampCount;
import com.capacity.microservice_capacity.domain.model.CapacityTechnologySummary;
import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ICapacityBootcampServicePort {
    Mono<Boolean> associateCapabilityToBootcamp(List<Long> capacityIds, Long bootcampId);
    Flux<CapacityBootcampCount> getAllBootcampRelationCounts();
    Flux<CapacityWithTechnologies> getCapacitiesWithTechnologiesByBootcamp(Long bootcampId);
    Mono<Void> deleteCapacitiesExclusivelyByBootcampId(Long bootcampId);
    Mono<CapacityTechnologySummary> getBootcampCapacityTechnologySummary(Long bootcampId);

}
