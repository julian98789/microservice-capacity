package com.capacity.microservice_capacity.infrastructure.entrypoints;

import com.capacity.microservice_capacity.infrastructure.entrypoints.handler.CapacityBootcampHandlerImpl;
import com.capacity.microservice_capacity.infrastructure.entrypoints.handler.CapacityHandlerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock
    private CapacityHandlerImpl capacityHandler;

    @Mock
    private CapacityBootcampHandlerImpl capacityBootcampHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        RouterRest routerRest = new RouterRest();
        webTestClient = WebTestClient.bindToRouterFunction(
                routerRest.routerFunction(capacityHandler, capacityBootcampHandler)
        ).build();

        lenient().when(capacityHandler.createCapacity(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(capacityHandler.listCapacities(any())).thenReturn(ServerResponse.ok().build());

        lenient().when(capacityBootcampHandler.associateCapacityBootcamp(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(capacityBootcampHandler.getAllBootcampRelationCounts(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(capacityBootcampHandler.getCapacitiesAndTechnologiesByBootcamp(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(capacityBootcampHandler.deleteCapacitiesByBootcampId(any())).thenReturn(ServerResponse.noContent().build());
    }

    @Test
    void testCreateCapacityRoute() {
        webTestClient.post().uri("/capacity")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testListCapacitiesRoute() {
        webTestClient.get().uri("/capacity/list")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testAssociateCapacityBootcampRoute() {
        webTestClient.post().uri("/capacity/bootcamp/associate")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetAllBootcampRelationCountsRoute() {
        webTestClient.get().uri("/capacity/bootcamp/relation-counts")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetCapacitiesAndTechnologiesByBootcampRoute() {
        webTestClient.get().uri("/capacity/bootcamp/capacities-technologies")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testDeleteCapacitiesByBootcampIdRoute() {
        webTestClient.delete().uri("/capacity/bootcamp/1/exclusive-delete")
                .exchange()
                .expectStatus().isNoContent();
    }
}
