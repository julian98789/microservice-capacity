package com.capacity.microservice_capacity.domain.spi;

import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import reactor.core.publisher.Flux;

public interface ICapacityQueryPort {
    Flux<CapacityWithTechnologies> findAllWithTechnologiesPagedAndSorted(
            int page,
            int size,
            String sortBy,       // "name" o "technologyCount"
            String direction     // "asc" o "desc"
    );
}