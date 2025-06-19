package com.capacity.microservice_capacity.infrastructure.entrypoints.handler;
import com.capacity.microservice_capacity.domain.api.ICapacityServicePort;
import com.capacity.microservice_capacity.domain.enums.TechnicalMessage;
import com.capacity.microservice_capacity.domain.exceptions.BusinessException;
import com.capacity.microservice_capacity.domain.exceptions.TechnicalException;
import com.capacity.microservice_capacity.domain.model.Capacity;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityDTO;
import com.capacity.microservice_capacity.infrastructure.entrypoints.mapper.ICapacityMapper;
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
class CapacityHandlerImplTest {

    @Mock
    private ICapacityServicePort capacityServicePort;
    @Mock
    private ICapacityMapper capacityMapper;

    private CapacityHandlerImpl handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new CapacityHandlerImpl(capacityServicePort, capacityMapper);
    }

    @Test
    void createCapacity_success() {
        ServerRequest request = mock(ServerRequest.class);
        CapacityDTO dto = new CapacityDTO(1L, "Cap", "Desc", List.of(1L, 2L));
        Capacity cap = new Capacity(1L, "Cap", "Desc");

        when(request.bodyToMono(CapacityDTO.class)).thenReturn(Mono.just(dto));
        when(capacityMapper.capacityDTOToTechnology(dto)).thenReturn(cap);
        when(capacityServicePort.registerCapacityWithTechnologies(cap, dto.getTechnologyIds())).thenReturn(Mono.just("Creado"));

        ServerResponse response = handler.createCapacity(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.statusCode());
    }

    @Test
    void createCapacity_businessException() {
        ServerRequest request = mock(ServerRequest.class);
        CapacityDTO dto = new CapacityDTO(1L, "Cap", "Desc", List.of(1L));
        Capacity cap = new Capacity(1L, "Cap", "Desc");

        when(request.bodyToMono(CapacityDTO.class)).thenReturn(Mono.just(dto));
        when(capacityMapper.capacityDTOToTechnology(dto)).thenReturn(cap);
        when(capacityServicePort.registerCapacityWithTechnologies(cap, dto.getTechnologyIds()))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.DUPLICATE_CAPACITY_ID)));

        ServerResponse response = handler.createCapacity(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
    }

    @Test
    void createCapacity_technicalException() {
        ServerRequest request = mock(ServerRequest.class);
        CapacityDTO dto = new CapacityDTO(1L, "Cap", "Desc", List.of(1L));
        Capacity cap = new Capacity(1L, "Cap", "Desc");

        when(request.bodyToMono(CapacityDTO.class)).thenReturn(Mono.just(dto));
        when(capacityMapper.capacityDTOToTechnology(dto)).thenReturn(cap);
        when(capacityServicePort.registerCapacityWithTechnologies(cap, dto.getTechnologyIds()))
                .thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

        ServerResponse response = handler.createCapacity(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
    }

    @Test
    void createCapacity_unexpectedException() {
        ServerRequest request = mock(ServerRequest.class);
        CapacityDTO dto = new CapacityDTO(1L, "Cap", "Desc", List.of(1L));
        Capacity cap = new Capacity(1L, "Cap", "Desc");

        when(request.bodyToMono(CapacityDTO.class)).thenReturn(Mono.just(dto));
        when(capacityMapper.capacityDTOToTechnology(dto)).thenReturn(cap);
        when(capacityServicePort.registerCapacityWithTechnologies(cap, dto.getTechnologyIds()))
                .thenReturn(Mono.error(new RuntimeException("Unexpected")));

        ServerResponse response = handler.createCapacity(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
    }

    @Test
    void listCapacities_success() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("page")).thenReturn(java.util.Optional.of("0"));
        when(request.queryParam("size")).thenReturn(java.util.Optional.of("10"));
        when(request.queryParam("sortBy")).thenReturn(java.util.Optional.of("name"));
        when(request.queryParam("direction")).thenReturn(java.util.Optional.of("asc"));

        when(capacityServicePort.listCapacitiesPaginated(0, 10, "name", "asc"))
                .thenReturn(Flux.empty());

        ServerResponse response = handler.listCapacities(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
    }
}