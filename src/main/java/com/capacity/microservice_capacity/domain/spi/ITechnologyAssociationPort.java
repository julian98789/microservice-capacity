package com.capacity.microservice_capacity.domain.spi;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ITechnologyAssociationPort {
    Mono<Boolean> associateTechnologiesToCapacity(Long capacityId, List<Long> technologyIds);
    Mono<Void> deleteTechnologiesExclusivelyByCapacityId(Long capacityId);
    Mono<Long> getTechnologyCountByCapacityId(Long capacityId);

}