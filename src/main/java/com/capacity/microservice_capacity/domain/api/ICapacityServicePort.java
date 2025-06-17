package com.capacity.microservice_capacity.domain.api;


import com.capacity.microservice_capacity.domain.model.Capacity;
import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ICapacityServicePort {
    Mono<String> registerCapacityWithTechnologies(Capacity capacity, List<Long> technologyIds);
    Flux<CapacityWithTechnologies> listCapacitiesPaginated(int page, int size, String sortBy, String direction);
}
