package com.capacity.microservice_capacity.infrastructure.entrypoints;

import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityBootcampAssociateRequestDTO;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityBootcampCountDTO;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityDTO;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityWithTechnologiesDTO;
import com.capacity.microservice_capacity.infrastructure.entrypoints.handler.CapacityBootcampHandlerImpl;
import com.capacity.microservice_capacity.infrastructure.entrypoints.handler.CapacityHandlerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/capacity",
                    method = {RequestMethod.POST},
                    beanClass = CapacityHandlerImpl.class,
                    beanMethod = "createCapacity",
                    operation = @Operation(
                            operationId = "createCapacity",
                            summary = "Crea una nueva capacidad",
                            description = "Crea una nueva capacidad en el sistema.",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = CapacityDTO.class),
                                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    value = "{ \"id\": 1, \"name\": \"Capacidad A\", \"description\": \"Descripción\", \"technologyIds\": [1,2] }"
                                            )
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Capacidad creada exitosamente",
                                            content = @Content(
                                                    schema = @Schema(implementation = CapacityDTO.class),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "{ \"id\": 1, \"name\": \"Capacidad A\", \"description\": \"Descripción\", \"technologyIds\": [1,2] }"
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Solicitud inválida"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/capacity/list",
                    method = {RequestMethod.GET},
                    beanClass = CapacityHandlerImpl.class,
                    beanMethod = "listCapacities",
                    operation = @Operation(
                            operationId = "listCapacities",
                            summary = "Lista todas las capacidades",
                            description = "Obtiene una lista de todas las capacidades.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de capacidades",
                                            content = @Content(
                                                    schema = @Schema(implementation = CapacityDTO.class, type = "array"),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "[{ \"id\": 1, \"name\": \"Capacidad A\", \"description\": \"Descripción\", \"technologyIds\": [1,2] }]"
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/capacity/bootcamp/associate",
                    method = {RequestMethod.POST},
                    beanClass = CapacityBootcampHandlerImpl.class,
                    beanMethod = "associateCapacityBootcamp",
                    operation = @Operation(
                            operationId = "associateCapacityBootcamp",
                            summary = "Asocia capacidades a un bootcamp",
                            description = "Asocia una lista de capacidades a un bootcamp específico.",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = CapacityBootcampAssociateRequestDTO.class),
                                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    value = "{ \"capacityIds\": [1,2], \"bootcampId\": 10 }"
                                            )
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Asociación exitosa"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Solicitud inválida"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/capacity/bootcamp/relation-counts",
                    method = {RequestMethod.GET},
                    beanClass = CapacityBootcampHandlerImpl.class,
                    beanMethod = "getAllBootcampRelationCounts",
                    operation = @Operation(
                            operationId = "getAllBootcampRelationCounts",
                            summary = "Obtiene el conteo de relaciones de capacidades por bootcamp",
                            description = "Devuelve el número de relaciones de capacidades asociadas a cada bootcamp.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Conteos de relaciones",
                                            content = @Content(
                                                    schema = @Schema(implementation = CapacityBootcampCountDTO.class, type = "array"),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "[{ \"bootcampId\": 10, \"relationCount\": 3 }]"
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/capacity/bootcamp/capacities-technologies",
                    method = {RequestMethod.GET},
                    beanClass = CapacityBootcampHandlerImpl.class,
                    beanMethod = "getCapacitiesAndTechnologiesByBootcamp",
                    operation = @Operation(
                            operationId = "getCapacitiesAndTechnologiesByBootcamp",
                            summary = "Obtiene capacidades y tecnologías asociadas a un bootcamp",
                            description = "Devuelve una lista de capacidades y sus tecnologías asociadas para un bootcamp.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de capacidades con tecnologías",
                                            content = @Content(
                                                    schema = @Schema(implementation = CapacityWithTechnologiesDTO.class, type = "array"),
                                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                            value = "[{ \"id\": 1, \"name\": \"Capacidad A\", \"technologies\": [{ \"id\": 100, \"name\": \"Java\" }] }]"
                                                    )
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/capacity/bootcamp/{bootcampId}/exclusive-delete",
                    method = {RequestMethod.DELETE},
                    beanClass = CapacityBootcampHandlerImpl.class,
                    beanMethod = "deleteCapacitiesByBootcampId",
                    operation = @Operation(
                            operationId = "deleteCapacitiesByBootcampId",
                            summary = "Elimina capacidades asociadas exclusivamente a un bootcamp",
                            description = "Elimina todas las capacidades que están asociadas únicamente al bootcamp indicado.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "Eliminación exitosa"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno"
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(CapacityHandlerImpl technologyHandler,
                                                         CapacityBootcampHandlerImpl capacityBootcampHandler) {
        return route(POST("/capacity"), technologyHandler::createCapacity)
                .andRoute(GET("/capacity/list"), technologyHandler::listCapacities)
                .andRoute(POST("/capacity/bootcamp/associate"),
                        capacityBootcampHandler::associateCapacityBootcamp)
                .andRoute(
                        GET("/capacity/bootcamp/relation-counts"),
                        capacityBootcampHandler::getAllBootcampRelationCounts)
                .andRoute(
                        GET("/capacity/bootcamp/capacities-technologies"),
                        capacityBootcampHandler::getCapacitiesAndTechnologiesByBootcamp)
                .andRoute(DELETE("/capacity/bootcamp/{bootcampId}/exclusive-delete"),
                        capacityBootcampHandler::deleteCapacitiesByBootcampId);
    }
}
