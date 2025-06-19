package com.capacity.microservice_capacity.domain.usecase;


import com.capacity.microservice_capacity.domain.api.ICapacityServicePort;
import com.capacity.microservice_capacity.domain.enums.TechnicalMessage;
import com.capacity.microservice_capacity.domain.exceptions.BusinessException;
import com.capacity.microservice_capacity.domain.model.Capacity;
import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import com.capacity.microservice_capacity.domain.spi.ICapacityPersistencePort;
import com.capacity.microservice_capacity.domain.spi.ICapacityQueryPort;
import com.capacity.microservice_capacity.domain.spi.ITechnologyAssociationPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CapacityUseCase implements ICapacityServicePort {

    private final ICapacityPersistencePort capacityPersistencePort;
    private final ITechnologyAssociationPort technologyAssociationPort;
    private final ICapacityQueryPort capacityQueryPort;

    public CapacityUseCase(ICapacityPersistencePort capacityPersistencePort,
                           ITechnologyAssociationPort technologyAssociationPort,
                           ICapacityQueryPort capacityQueryPort) {
        this.capacityPersistencePort = capacityPersistencePort;
        this.technologyAssociationPort = technologyAssociationPort;
        this.capacityQueryPort = capacityQueryPort;
    }

    @Override
    public Mono<String> registerCapacityWithTechnologies(Capacity capacity, List<Long> technologyIds) {
        return validateCapacity(capacity)
                .then(validateTechnologyIds(technologyIds))
                .then(Mono.defer(() -> capacityPersistencePort.existsByName(capacity.name())
                        .flatMap(exists -> {
                            if (Boolean.TRUE.equals(exists)) {
                                return Mono.error(new BusinessException(TechnicalMessage.CAPACITY_ALREADY_EXISTS));
                            }
                            return capacityPersistencePort.save(capacity)
                                    .flatMap(savedCapacity ->
                                            technologyAssociationPort.associateTechnologiesToCapacity(savedCapacity.id(), technologyIds)
                                                    .flatMap(success -> {
                                                        if (Boolean.TRUE.equals(success)) {
                                                            return Mono.just(TechnicalMessage.CAPACITY_CREATED.name());
                                                        } else {
                                                            return capacityPersistencePort.deleteById(savedCapacity.id())
                                                                    .then(Mono.error(new BusinessException(TechnicalMessage.CAPACITY_ASSOCIATION_FAILED)));
                                                        }
                                                    })
                                    );
                        })));
    }

    @Override
    public Flux<CapacityWithTechnologies> listCapacitiesPaginated(int page, int size, String sortBy, String direction) {
        return capacityQueryPort.findAllWithTechnologiesPagedAndSorted(page, size, sortBy, direction);
    }

    private Mono<Void> validateCapacity(Capacity capacity) {
        if (capacity.name() == null || capacity.name().isBlank() || capacity.name().length() > 50) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_CAPACITY_NAME));
        }
        if (capacity.description() == null || capacity.description().isBlank() || capacity.description().length() > 90) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_CAPACITY_DESCRIPTION));
        }
        return Mono.empty();
    }

    private Mono<Void> validateTechnologyIds(List<Long> technologyIds) {
        if (technologyIds == null || technologyIds.size() < 3) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_TECHNOLOGY_LIST));
        }
        if (technologyIds.size() > 20) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_TECHNOLOGY_LIST));
        }
        Set<Long> uniqueIds = new HashSet<>(technologyIds);
        if (uniqueIds.size() != technologyIds.size()) {
            return Mono.error(new BusinessException(TechnicalMessage.DUPLICATE_TECHNOLOGY_ID));
        }
        return Mono.empty();
    }

}

