package com.capacity.microservice_capacity.infrastructure.entrypoints;

import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityDTO;
import com.capacity.microservice_capacity.infrastructure.entrypoints.handler.CapacityBootcampHandlerImpl;
import com.capacity.microservice_capacity.infrastructure.entrypoints.handler.CapacityHandlerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperation(
            path = "/capacity",
            method = {RequestMethod.POST},
            beanClass = CapacityHandlerImpl.class,
            beanMethod = "createCapacity",
            operation = @Operation(
                    operationId = "createCapacity",
                    summary = "Creates a new capacity",
                    requestBody = @RequestBody(
                            required = true,
                            content = @Content(schema = @Schema(implementation = CapacityDTO.class))
                    ),
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Capacity successfully created",
                                    content = @Content(schema = @Schema(implementation = CapacityDTO.class))
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Invalid request"
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> routerFunction(CapacityHandlerImpl technologyHandler,
                                                         CapacityBootcampHandlerImpl capacityBootcampHandler) {
        return route(POST("/capacity"), technologyHandler::createCapacity)
                .andRoute(GET("/capacity/list"), technologyHandler::listCapacities)
                .andRoute(POST("/capacity/bootcamp/associate"),
                        capacityBootcampHandler::associateCapacityBootcamp);
    }
}