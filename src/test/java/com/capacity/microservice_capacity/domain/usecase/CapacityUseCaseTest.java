package com.capacity.microservice_capacity.domain.usecase;

import com.capacity.microservice_capacity.domain.enums.TechnicalMessage;
import com.capacity.microservice_capacity.domain.exceptions.BusinessException;
import com.capacity.microservice_capacity.domain.model.Capacity;
import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import com.capacity.microservice_capacity.domain.spi.ICapacityPersistencePort;
import com.capacity.microservice_capacity.domain.spi.ICapacityQueryPort;
import com.capacity.microservice_capacity.domain.spi.ITechnologyAssociationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapacityUseCaseTest {

    @Mock
    private ICapacityPersistencePort capacityPersistencePort;
    @Mock
    private ITechnologyAssociationPort technologyAssociationPort;
    @Mock
    private ICapacityQueryPort capacityQueryPort;

    private CapacityUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CapacityUseCase(
                capacityPersistencePort,
                technologyAssociationPort,
                capacityQueryPort
        );
    }

    @Test
    void registerCapacityWithTechnologies_success() {
        Capacity capacity = new Capacity(1L, "Capacidad", "Descripción");
        List<Long> techIds = List.of(1L, 2L, 3L);

        when(capacityPersistencePort.existsByName("Capacidad")).thenReturn(Mono.just(false));
        when(capacityPersistencePort.save(capacity)).thenReturn(Mono.just(capacity));
        when(technologyAssociationPort.associateTechnologiesToCapacity(1L, techIds)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.registerCapacityWithTechnologies(capacity, techIds))
                .expectNext(TechnicalMessage.CAPACITY_CREATED.name())
                .verifyComplete();
    }

    @Test
    void registerCapacityWithTechnologies_duplicateTechnologies() {
        Capacity capacity = new Capacity(1L, "Capacidad", "Descripción");
        List<Long> techIds = List.of(1L, 2L, 2L);

        StepVerifier.create(useCase.registerCapacityWithTechnologies(capacity, techIds))
                .expectErrorSatisfies(e -> {
                    assert e instanceof BusinessException;
                    assert ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.DUPLICATE_TECHNOLOGY_ID;
                })
                .verify();
    }

    @Test
    void registerCapacityWithTechnologies_invalidName() {
        Capacity capacity = new Capacity(1L, "", "Descripción");
        List<Long> techIds = List.of(1L, 2L, 3L);

        StepVerifier.create(useCase.registerCapacityWithTechnologies(capacity, techIds))
                .expectErrorSatisfies(e -> {
                    assert e instanceof BusinessException;
                    assert ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.INVALID_CAPACITY_NAME;
                })
                .verify();
    }

    @Test
    void registerCapacityWithTechnologies_invalidDescription() {
        Capacity capacity = new Capacity(1L, "Capacidad", "");
        List<Long> techIds = List.of(1L, 2L, 3L);

        StepVerifier.create(useCase.registerCapacityWithTechnologies(capacity, techIds))
                .expectErrorSatisfies(e -> {
                    assert e instanceof BusinessException;
                    assert ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.INVALID_CAPACITY_DESCRIPTION;
                })
                .verify();
    }

    @Test
    void registerCapacityWithTechnologies_invalidTechList() {
        Capacity capacity = new Capacity(1L, "Capacidad", "Descripción");
        List<Long> techIds = List.of(1L, 2L);

        StepVerifier.create(useCase.registerCapacityWithTechnologies(capacity, techIds))
                .expectErrorSatisfies(e -> {
                    assert e instanceof BusinessException;
                    assert ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.INVALID_TECHNOLOGY_LIST;
                })
                .verify();
    }

    @Test
    void registerCapacityWithTechnologies_capacityAlreadyExists() {
        Capacity capacity = new Capacity(1L, "Capacidad", "Descripción");
        List<Long> techIds = List.of(1L, 2L, 3L);

        when(capacityPersistencePort.existsByName("Capacidad")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.registerCapacityWithTechnologies(capacity, techIds))
                .expectErrorSatisfies(e -> {
                    assert e instanceof BusinessException;
                    assert ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.CAPACITY_ALREADY_EXISTS;
                })
                .verify();
    }

    @Test
    void registerCapacityWithTechnologies_associationFailed() {
        Capacity capacity = new Capacity(1L, "Capacidad", "Descripción");
        List<Long> techIds = List.of(1L, 2L, 3L);

        when(capacityPersistencePort.existsByName("Capacidad")).thenReturn(Mono.just(false));
        when(capacityPersistencePort.save(capacity)).thenReturn(Mono.just(capacity));
        when(technologyAssociationPort.associateTechnologiesToCapacity(1L, techIds)).thenReturn(Mono.just(false));
        when(capacityPersistencePort.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.registerCapacityWithTechnologies(capacity, techIds))
                .expectErrorSatisfies(e -> {
                    assert e instanceof BusinessException;
                    assert ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.CAPACITY_ASSOCIATION_FAILED;
                })
                .verify();
    }

    @Test
    void listCapacitiesPaginated_success() {
        CapacityWithTechnologies cwt = new CapacityWithTechnologies(1L, "Cap", "Desc", 3L, List.of());
        when(capacityQueryPort.findAllWithTechnologiesPagedAndSorted(0, 10, "name", "asc"))
                .thenReturn(Flux.just(cwt));

        StepVerifier.create(useCase.listCapacitiesPaginated(0, 10, "name", "asc"))
                .expectNext(cwt)
                .verifyComplete();
    }
}