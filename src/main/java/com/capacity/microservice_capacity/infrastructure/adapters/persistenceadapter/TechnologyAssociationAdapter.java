package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter;


import com.capacity.microservice_capacity.domain.spi.ITechnologyAssociationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TechnologyAssociationAdapter implements ITechnologyAssociationPort {

    private final WebClient webClient;

    @Value("${technology.service.url:http://localhost:8080}")
    private String technologyServiceUrl;

    @Override
    public Mono<Boolean> associateTechnologiesToCapacity(Long capacityId, List<Long> technologyIds) {
        Map<String, Object> body = Map.of(
                "capabilityId", capacityId,
                "technologyIds", technologyIds
        );
        return webClient.post()
                .uri(technologyServiceUrl + "/technology/capability/associate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> true)
                .onErrorResume(e -> Mono.just(false));
    }


    @Override
    public Mono<Void> deleteTechnologiesExclusivelyByCapacityId(Long capacityId) {
        return webClient.delete()
                .uri(technologyServiceUrl + "/technology/capability/{capabilityId}/exclusive-delete", capacityId)
                .retrieve()
                .bodyToMono(Void.class);
    }
}