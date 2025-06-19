package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter;

import com.capacity.microservice_capacity.domain.model.TechnologySummary;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.entity.CapacityEntity;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.repository.ICapacityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Answers;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityQueryAdapterTest {

    @Mock
    private ICapacityRepository capacityRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    private CapacityQueryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CapacityQueryAdapter(capacityRepository, webClient);
    }

    @Test
    void testFindAllWithTechnologiesPagedAndSorted() {
        CapacityEntity capacityEntity = new CapacityEntity();
        capacityEntity.setId(1L);
        capacityEntity.setName("Capacidad A");
        capacityEntity.setDescription("Descripción");

        // Simula datos desde el repositorio
        when(capacityRepository.findAll()).thenReturn(Flux.just(capacityEntity));

        // Simula conteo de tecnologías desde otro microservicio
        when(webClient.get()
                .uri("http://localhost:8080/technology/capability/relation-counts")
                .retrieve()
                .bodyToFlux(Map.class))
                .thenReturn(Flux.just(Map.of("capabilityId", 1L, "technologyCount", 3L)));

        // Simula detalle de tecnologías
        when(webClient.get()
                .uri("http://localhost:8080/technology/capability/{capabilityId}/technologies", 1L)
                .retrieve()
                .bodyToFlux(TechnologySummary.class))
                .thenReturn(Flux.just(
                        new TechnologySummary(1L, "Tech 1"),
                        new TechnologySummary(2L, "Tech 2")
                ));

        StepVerifier.create(adapter.findAllWithTechnologiesPagedAndSorted(0, 10, "technologyCount", "asc"))
                .expectNextMatches(cap -> cap.id().equals(1L)
                        && cap.name().equals("Capacidad A")
                        && cap.technologyCount() == 2L)
                .verifyComplete();
    }

    @Test
    void testFindAllWithTechnologiesByIds() {
        CapacityEntity capacityEntity = new CapacityEntity();
        capacityEntity.setId(2L);
        capacityEntity.setName("Capacidad B");
        capacityEntity.setDescription("Desc");

        when(capacityRepository.findAllById(List.of(2L)))
                .thenReturn(Flux.just(capacityEntity));

        when(webClient.get()
                .uri("http://localhost:8080/technology/capability/{capabilityId}/technologies", 2L)
                .retrieve()
                .bodyToFlux(TechnologySummary.class))
                .thenReturn(Flux.just(
                        new TechnologySummary(1L, "Tech 1"),
                        new TechnologySummary(2L, "Tech 2")
                ));

        StepVerifier.create(adapter.findAllWithTechnologiesByIds(List.of(2L)))
                .expectNextMatches(cap -> cap.id().equals(2L)
                        && cap.technologyCount() == 2L)
                .verifyComplete();
    }
}
