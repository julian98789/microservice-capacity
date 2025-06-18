package com.capacity.microservice_capacity.domain.spi;

import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ICapacityQueryPort {
    Flux<CapacityWithTechnologies> findAllWithTechnologiesPagedAndSorted(
            int page,
            int size,
            String sortBy,
            String direction
    );

    Flux<CapacityWithTechnologies> findAllWithTechnologiesByIds(List<Long> capacityIds);


}