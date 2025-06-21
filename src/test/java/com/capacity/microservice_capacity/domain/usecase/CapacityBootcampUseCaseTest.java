package com.capacity.microservice_capacity.domain.usecase;

import com.capacity.microservice_capacity.domain.enums.TechnicalMessage;
import com.capacity.microservice_capacity.domain.exceptions.BusinessException;
import com.capacity.microservice_capacity.domain.model.CapacityBootcamp;
import com.capacity.microservice_capacity.domain.model.CapacityBootcampCount;
import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import com.capacity.microservice_capacity.domain.spi.ICapacityBootcampPersistencePort;
import com.capacity.microservice_capacity.domain.spi.ICapacityPersistencePort;
import com.capacity.microservice_capacity.domain.spi.ICapacityQueryPort;
import com.capacity.microservice_capacity.domain.spi.ITechnologyAssociationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapacityBootcampUseCaseTest {

    @Mock
    private ICapacityBootcampPersistencePort capacityBootcampPersistencePort;
    @Mock
    private ICapacityPersistencePort capacityPersistencePort;
    @Mock
    private ICapacityQueryPort capacityQueryPort;
    @Mock
    private ITechnologyAssociationPort technologyAssociationPort;

    private CapacityBootcampUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new CapacityBootcampUseCase(
                capacityBootcampPersistencePort,
                capacityPersistencePort,
                capacityQueryPort,
                technologyAssociationPort
        );
    }

    @Test
    void associateCapabilityToBootcamp_success() {
        List<Long> capacityIds = List.of(1L, 2L);
        Long bootcampId = 10L;

        when(capacityPersistencePort.existsById(anyLong())).thenReturn(Mono.just(true));
        when(capacityBootcampPersistencePort.findByCapacityIds(anyList())).thenReturn(Flux.empty());
        when(capacityBootcampPersistencePort.findByBootcampId(anyLong())).thenReturn(Flux.empty());
        when(capacityBootcampPersistencePort.saveAll(anyList())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.associateCapabilityToBootcamp(capacityIds, bootcampId))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void associateCapabilityToBootcamp_duplicateIds() {
        List<Long> capacityIds = List.of(1L, 1L);
        Long bootcampId = 10L;
        when(capacityBootcampPersistencePort.findByCapacityIds(anyList())).thenReturn(Flux.empty());
        when(capacityBootcampPersistencePort.findByBootcampId(anyLong())).thenReturn(Flux.empty()); // <-- Agrega esta línea

        StepVerifier.create(useCase.associateCapabilityToBootcamp(capacityIds, bootcampId))
                .expectErrorSatisfies(e -> {
                    assert e instanceof BusinessException;
                    assert ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.DUPLICATE_CAPACITY_ID;
                })
                .verify();
    }

    @Test
    void associateCapabilityToBootcamp_capacityNotFound() {
        List<Long> capacityIds = List.of(1L, 2L);
        Long bootcampId = 10L;

        when(capacityPersistencePort.existsById(1L)).thenReturn(Mono.just(true));
        when(capacityPersistencePort.existsById(2L)).thenReturn(Mono.just(false));
        when(capacityBootcampPersistencePort.findByCapacityIds(anyList())).thenReturn(Flux.empty());
        when(capacityBootcampPersistencePort.findByBootcampId(anyLong())).thenReturn(Flux.empty()); // <-- Agrega esta línea

        StepVerifier.create(useCase.associateCapabilityToBootcamp(capacityIds, bootcampId))
                .expectErrorSatisfies(e -> {
                    assert e instanceof BusinessException;
                    assert ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.CAPACITY_NOT_FOUND;
                })
                .verify();
    }

    @Test
    void associateCapabilityToBootcamp_alreadyAssociated() {
        List<Long> capacityIds = List.of(1L);
        Long bootcampId = 10L;

        when(capacityPersistencePort.existsById(anyLong())).thenReturn(Mono.just(true));
        when(capacityBootcampPersistencePort.findByCapacityIds(anyList()))
                .thenReturn(Flux.just(new CapacityBootcamp(1L, 1L, bootcampId)));
        when(capacityBootcampPersistencePort.findByBootcampId(anyLong())).thenReturn(Flux.empty());

        StepVerifier.create(useCase.associateCapabilityToBootcamp(capacityIds, bootcampId))
                .expectErrorSatisfies(e -> {
                    assert e instanceof BusinessException;
                    assert ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.CAPACITY_ALREADY_ASSOCIATED;
                })
                .verify();
    }

    @Test
    void associateCapabilityToBootcamp_limitExceeded() {
        List<Long> capacityIds = List.of(1L, 2L, 3L, 4L);
        Long bootcampId = 10L;

        when(capacityPersistencePort.existsById(anyLong())).thenReturn(Mono.just(true));
        when(capacityBootcampPersistencePort.findByCapacityIds(anyList())).thenReturn(Flux.empty());
        when(capacityBootcampPersistencePort.findByBootcampId(anyLong())).thenReturn(Flux.just(new CapacityBootcamp(1L, 5L, bootcampId)));

        StepVerifier.create(useCase.associateCapabilityToBootcamp(capacityIds, bootcampId))
                .expectErrorSatisfies(e -> {
                    assert e instanceof BusinessException;
                    assert ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.CAPABILITY_CAPACITY_LIMIT;
                })
                .verify();
    }

    @Test
    void getAllBootcampRelationCounts_success() {
        CapacityBootcampCount count = new CapacityBootcampCount(1L, 2L);
        when(capacityBootcampPersistencePort.getAllBootcampRelationCounts()).thenReturn(Flux.just(count));

        StepVerifier.create(useCase.getAllBootcampRelationCounts())
                .expectNext(count)
                .verifyComplete();
    }

    @Test
    void getCapacitiesWithTechnologiesByBootcamp_success() {
        Long bootcampId = 10L;
        CapacityBootcamp cb = new CapacityBootcamp(1L, 2L, bootcampId);
        CapacityWithTechnologies cwt = new CapacityWithTechnologies(
                2L,
                "Cap",
                "Descripción",
                1L,
                List.of()
        );
        when(capacityBootcampPersistencePort.findByBootcampId(bootcampId)).thenReturn(Flux.just(cb));
        when(capacityQueryPort.findAllWithTechnologiesByIds(List.of(2L))).thenReturn(Flux.just(cwt));

        StepVerifier.create(useCase.getCapacitiesWithTechnologiesByBootcamp(bootcampId))
                .expectNext(cwt)
                .verifyComplete();
    }

    @Test
    void deleteCapacitiesExclusivelyByBootcampId_onlyOneAssociation() {
        Long bootcampId = 10L;
        Long capacityId = 2L;
        CapacityBootcamp cb = new CapacityBootcamp(1L, capacityId, bootcampId);

        when(capacityBootcampPersistencePort.findByBootcampId(bootcampId)).thenReturn(Flux.just(cb));
        when(capacityBootcampPersistencePort.findByCapacityIds(List.of(capacityId)))
                .thenReturn(Flux.just(cb));
        when(technologyAssociationPort.deleteTechnologiesExclusivelyByCapacityId(capacityId)).thenReturn(Mono.empty());
        when(capacityPersistencePort.deleteById(capacityId)).thenReturn(Mono.empty());
        when(capacityBootcampPersistencePort.deleteByCapacityIdAndBootcampId(capacityId, bootcampId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteCapacitiesExclusivelyByBootcampId(bootcampId))
                .verifyComplete();
    }

    @Test
    void deleteCapacitiesExclusivelyByBootcampId_multipleAssociations() {
        Long bootcampId = 10L;
        Long capacityId = 2L;
        CapacityBootcamp cb1 = new CapacityBootcamp(1L, capacityId, bootcampId);
        CapacityBootcamp cb2 = new CapacityBootcamp(2L, capacityId, 99L);

        when(capacityBootcampPersistencePort.findByBootcampId(bootcampId)).thenReturn(Flux.just(cb1));
        when(capacityBootcampPersistencePort.findByCapacityIds(List.of(capacityId)))
                .thenReturn(Flux.just(cb1, cb2));
        when(capacityBootcampPersistencePort.deleteByCapacityIdAndBootcampId(capacityId, bootcampId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteCapacitiesExclusivelyByBootcampId(bootcampId))
                .verifyComplete();
    }

    @Test
    void getBootcampCapacityTechnologySummary_success() {
        Long bootcampId = 10L;
        List<Long> capacityIds = List.of(1L, 2L, 3L);

        when(capacityBootcampPersistencePort.findByBootcampId(bootcampId))
                .thenReturn(Flux.fromIterable(capacityIds.stream()
                        .map(id -> new CapacityBootcamp(null, id, bootcampId))
                        .toList()));

        when(technologyAssociationPort.getTechnologyCountByCapacityId(1L)).thenReturn(Mono.just(2L));
        when(technologyAssociationPort.getTechnologyCountByCapacityId(2L)).thenReturn(Mono.just(3L));
        when(technologyAssociationPort.getTechnologyCountByCapacityId(3L)).thenReturn(Mono.just(5L));

        StepVerifier.create(useCase.getBootcampCapacityTechnologySummary(bootcampId))
                .expectNextMatches(summary ->
                        summary.capacityCount() == 3 && summary.totalTechnologyCount() == 10L)
                .verifyComplete();

        verify(capacityBootcampPersistencePort).findByBootcampId(bootcampId);
        verify(technologyAssociationPort).getTechnologyCountByCapacityId(1L);
        verify(technologyAssociationPort).getTechnologyCountByCapacityId(2L);
        verify(technologyAssociationPort).getTechnologyCountByCapacityId(3L);
    }

}