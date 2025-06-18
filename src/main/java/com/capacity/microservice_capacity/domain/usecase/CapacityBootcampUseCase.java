package com.capacity.microservice_capacity.domain.usecase;

import com.capacity.microservice_capacity.domain.api.ICapacityBootcampServicePort;
import com.capacity.microservice_capacity.domain.enums.TechnicalMessage;
import com.capacity.microservice_capacity.domain.exceptions.BusinessException;
import com.capacity.microservice_capacity.domain.model.CapacityBootcamp;
import com.capacity.microservice_capacity.domain.model.CapacityBootcampCount;
import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import com.capacity.microservice_capacity.domain.spi.ICapacityBootcampPersistencePort;
import com.capacity.microservice_capacity.domain.spi.ICapacityPersistencePort;
import com.capacity.microservice_capacity.domain.spi.ICapacityQueryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CapacityBootcampUseCase implements ICapacityBootcampServicePort {

    private final ICapacityBootcampPersistencePort capacityBootcampPersistencePort;
    private final ICapacityPersistencePort capacityPersistencePort;
    private final ICapacityQueryPort capacityQueryPort;

    public CapacityBootcampUseCase(ICapacityBootcampPersistencePort capacityBootcampPersistencePort,
                                   ICapacityPersistencePort capacityPersistencePort, ICapacityQueryPort capacityQueryPort) {
        this.capacityBootcampPersistencePort = capacityBootcampPersistencePort;
        this.capacityPersistencePort = capacityPersistencePort;
        this.capacityQueryPort = capacityQueryPort;
    }

    @Override
    public Mono<Boolean> associateCapabilityToBootcamp(List<Long> capacityIds, Long bootcampId) {
        return validateNoDuplicates(capacityIds)
                .then(validateTechnologiesExist(capacityIds))
                .then(checkAlreadyAssociated(capacityIds, bootcampId))
                .then(validateLimitNotExceeded(capacityIds, bootcampId))
                .flatMap(newAssociations ->
                        capacityBootcampPersistencePort.saveAll(newAssociations).thenReturn(true));
    }

    @Override
    public Flux<CapacityBootcampCount> getAllBootcampRelationCounts() {
        return capacityBootcampPersistencePort.getAllBootcampRelationCounts();
    }

    @Override
    public Flux<CapacityWithTechnologies> getCapacitiesWithTechnologiesByBootcamp(Long bootcampId) {
        return capacityBootcampPersistencePort.findByBootcampId(bootcampId)
                .map(CapacityBootcamp::capacityId)
                .collectList()
                .flatMapMany(capacityQueryPort::findAllWithTechnologiesByIds);
    }

    private Mono<Void> validateNoDuplicates(List<Long> technologyIds) {
        Set<Long> uniqueIds = new HashSet<>();
        Set<Long> duplicatedIds = new HashSet<>();
        for (Long id : technologyIds) {
            if (!uniqueIds.add(id)) {
                duplicatedIds.add(id);
            }
        }
        if (!duplicatedIds.isEmpty()) {
            return Mono.error(new BusinessException(
                    TechnicalMessage.DUPLICATE_CAPACITY_ID

            ));
        }
        return Mono.empty();
    }

    private Mono<Void> validateTechnologiesExist(List<Long> capacityIds) {
        return Flux.fromIterable(capacityIds)
                .flatMap(techId -> capacityPersistencePort.existsById(techId)
                        .flatMap(exists -> {
                            if (!exists) {
                                return Mono.error(new BusinessException(
                                        TechnicalMessage.CAPACITY_NOT_FOUND

                                ));
                            }
                            return Mono.just(true);
                        })
                )
                .then();
    }

    private Mono<Void> checkAlreadyAssociated(List<Long> capacityIds, Long bootcampId) {
        return capacityBootcampPersistencePort.findByCapacityIds(capacityIds)
                .filter(tc -> tc.bootcampId().equals(bootcampId))
                .map(CapacityBootcamp::capacityId)
                .collectList()
                .flatMap(alreadyAssociatedIds -> {
                    if (!alreadyAssociatedIds.isEmpty()) {
                        return Mono.error(
                                new BusinessException(
                                        TechnicalMessage.CAPACITY_ALREADY_ASSOCIATED

                                )
                        );
                    }
                    return Mono.empty();
                });
    }

    private Mono<List<CapacityBootcamp>> validateLimitNotExceeded(List<Long> capacityIds, Long bootcampId) {
        return capacityBootcampPersistencePort.findByBootcampId(bootcampId)
                .count()
                .flatMap(currentCount -> {
                    long total = currentCount + capacityIds.size();
                    if (total > 4) {
                        return Mono.error(new BusinessException(
                                TechnicalMessage.CAPABILITY_CAPACITY_LIMIT
                        ));
                    }
                    List<CapacityBootcamp> newAssociations = capacityIds.stream()
                            .map(techId -> new CapacityBootcamp(null, techId, bootcampId))
                            .toList();
                    return Mono.just(newAssociations);
                });
    }
}
