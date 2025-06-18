package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter;

import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import com.capacity.microservice_capacity.domain.model.TechnologySummary;
import com.capacity.microservice_capacity.domain.spi.ICapacityQueryPort;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.entity.CapacityEntity;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.repository.ICapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CapacityQueryAdapter implements ICapacityQueryPort {
    private final ICapacityRepository capacityRepository;
    private final WebClient webClient;

    @Override
    public Flux<CapacityWithTechnologies> findAllWithTechnologiesPagedAndSorted(
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        Flux<CapacityEntity> capacities = capacityRepository.findAll();

        Mono<Map<Long, Long>> techCountMapMono = (sortBy.equals("technologyCount"))
                ? getTechnologyCounts()
                : Mono.just(Collections.emptyMap());

        return techCountMapMono.flatMapMany(techCountMap -> capacities
                .map(entity -> new CapacityWithTechnologies(
                        entity.getId(),
                        entity.getName(),
                        entity.getDescription(),
                        techCountMap.getOrDefault(entity.getId(), 0L),
                        List.of()
                ))
                .sort(getComparator(sortBy, direction))
                .skip((long) page * size)
                .take(size)
                .flatMap(this::addTechnologies)
        );
    }

    @Override
    public Flux<CapacityWithTechnologies> findAllWithTechnologiesByIds(List<Long> capacityIds) {
        return capacityRepository.findAllById(capacityIds)
                .flatMap(entity ->
                        addTechnologies(new CapacityWithTechnologies(
                                entity.getId(),
                                entity.getName(),
                                entity.getDescription(),
                                null,
                                List.of()
                        ))
                );
    }



    private Comparator<CapacityWithTechnologies> getComparator(String sortBy, String direction) {
        Comparator<CapacityWithTechnologies> comparator = sortBy.equals("technologyCount")
                ? Comparator.comparing(CapacityWithTechnologies::technologyCount)
                : Comparator.comparing(CapacityWithTechnologies::name, String.CASE_INSENSITIVE_ORDER);

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }


    private Mono<Map<Long, Long>> getTechnologyCounts() {
        return webClient.get()
                .uri("http://localhost:8080/technology/capability/relation-counts")
                .retrieve()
                .bodyToFlux(Map.class)
                .collectMap(
                        m -> Long.valueOf(m.get("capabilityId").toString()),
                        m -> Long.valueOf(m.get("technologyCount").toString())
                );
    }


    private Mono<CapacityWithTechnologies> addTechnologies(CapacityWithTechnologies cap) {
        return webClient.get()
                .uri("http://localhost:8080/technology/capability/{capabilityId}/technologies", cap.id())
                .retrieve()
                .bodyToFlux(TechnologySummary.class)
                .collectList()
                .map(techs -> new CapacityWithTechnologies(
                        cap.id(),
                        cap.name(),
                        cap.description(),
                        (long) techs.size(),
                        techs
                ));
    }
}