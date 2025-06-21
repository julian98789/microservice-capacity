package com.capacity.microservice_capacity.infrastructure.entrypoints.handler;

import com.capacity.microservice_capacity.domain.api.ICapacityBootcampServicePort;
import com.capacity.microservice_capacity.domain.enums.TechnicalMessage;
import com.capacity.microservice_capacity.domain.exceptions.BusinessException;
import com.capacity.microservice_capacity.domain.exceptions.TechnicalException;
import com.capacity.microservice_capacity.domain.model.CapacityBootcampCount;
import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityBootcampAssociateRequestDTO;
import com.capacity.microservice_capacity.infrastructure.entrypoints.mapper.ICapacityTechnologySummaryMapper;
import com.capacity.microservice_capacity.infrastructure.entrypoints.mapper.ICapacityWithTechnologiesMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapacityBootcampHandlerImplTest {

    @Mock
    private ICapacityBootcampServicePort service;

    @Mock
    private ICapacityWithTechnologiesMapper mapper;

    @Mock
    ICapacityTechnologySummaryMapper capacityTechnologySummaryMapper;

    private CapacityBootcampHandlerImpl handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new CapacityBootcampHandlerImpl(service, mapper,capacityTechnologySummaryMapper);
    }

    @Test
    void associateCapacityBootcamp_success() {
        ServerRequest request = mock(ServerRequest.class);
        CapacityBootcampAssociateRequestDTO dto = new CapacityBootcampAssociateRequestDTO(List.of(1L), 2L);

        when(request.bodyToMono(CapacityBootcampAssociateRequestDTO.class)).thenReturn(Mono.just(dto));
        when(service.associateCapabilityToBootcamp(dto.getCapacityIds(), dto.getBootcampId()))
                .thenReturn(Mono.just(true));

        ServerResponse response = handler.associateCapacityBootcamp(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
    }

    @Test
    void associateCapacityBootcamp_businessException() {
        ServerRequest request = mock(ServerRequest.class);
        CapacityBootcampAssociateRequestDTO dto = new CapacityBootcampAssociateRequestDTO(List.of(1L), 2L);

        when(request.bodyToMono(CapacityBootcampAssociateRequestDTO.class)).thenReturn(Mono.just(dto));
        when(service.associateCapabilityToBootcamp(dto.getCapacityIds(), dto.getBootcampId()))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.DUPLICATE_CAPACITY_ID)));

        ServerResponse response = handler.associateCapacityBootcamp(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
    }

    @Test
    void associateCapacityBootcamp_technicalException() {
        ServerRequest request = mock(ServerRequest.class);
        CapacityBootcampAssociateRequestDTO dto = new CapacityBootcampAssociateRequestDTO(List.of(1L), 2L);

        when(request.bodyToMono(CapacityBootcampAssociateRequestDTO.class)).thenReturn(Mono.just(dto));
        when(service.associateCapabilityToBootcamp(dto.getCapacityIds(), dto.getBootcampId()))
                .thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

        ServerResponse response = handler.associateCapacityBootcamp(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
    }

    @Test
    void getAllBootcampRelationCounts_success() {
        ServerRequest request = mock(ServerRequest.class);
        when(service.getAllBootcampRelationCounts())
                .thenReturn(Flux.just(new CapacityBootcampCount(1L, 5L)));

        ServerResponse response = handler.getAllBootcampRelationCounts(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
    }

    @Test
    void getCapacitiesAndTechnologiesByBootcamp_success() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("bootcampId")).thenReturn(java.util.Optional.of("1"));
        when(service.getCapacitiesWithTechnologiesByBootcamp(1L))
                .thenReturn(Flux.just(mock(CapacityWithTechnologies.class)));
        when(mapper.toDTO(any())).thenReturn(mock(com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityWithTechnologiesDTO.class));

        ServerResponse response = handler.getCapacitiesAndTechnologiesByBootcamp(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
    }

    @Test
    void deleteCapacitiesByBootcampId_success() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.pathVariable("bootcampId")).thenReturn("1");
        when(service.deleteCapacitiesExclusivelyByBootcampId(1L)).thenReturn(Mono.empty());

        ServerResponse response = handler.deleteCapacitiesByBootcampId(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode());
    }

    @Test
    void getBootcampCapacityTechnologySummary_success() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("bootcampId")).thenReturn(java.util.Optional.of("1"));

        var summary = new com.capacity.microservice_capacity.domain.model.CapacityTechnologySummary(3L, 12L);
        var dto = new com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityTechnologySummaryDTO(3L, 12L);

        when(service.getBootcampCapacityTechnologySummary(1L)).thenReturn(Mono.just(summary));
        when(capacityTechnologySummaryMapper.toDTO(summary)).thenReturn(dto);

        ServerResponse response = handler.getBootcampCapacityTechnologySummary(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
    }

    @Test
    void getBootcampCapacityTechnologySummary_internalServerError() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("bootcampId")).thenReturn(java.util.Optional.of("1"));

        when(service.getBootcampCapacityTechnologySummary(1L)).thenReturn(Mono.error(new RuntimeException("Unexpected")));

        ServerResponse response = handler.getBootcampCapacityTechnologySummary(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
    }
}
