package com.capacity.microservice_capacity.infrastructure.entrypoints.handler;



import com.capacity.microservice_capacity.domain.api.ICapacityServicePort;
import com.capacity.microservice_capacity.domain.enums.TechnicalMessage;
import com.capacity.microservice_capacity.domain.exceptions.BusinessException;
import com.capacity.microservice_capacity.domain.exceptions.TechnicalException;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityDTO;
import com.capacity.microservice_capacity.infrastructure.entrypoints.mapper.ICapacityMapper;
import com.capacity.microservice_capacity.infrastructure.entrypoints.util.APIResponse;
import com.capacity.microservice_capacity.infrastructure.entrypoints.util.ErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static com.capacity.microservice_capacity.infrastructure.entrypoints.util.Constants.CAPACITY_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapacityHandlerImpl {

    private final ICapacityServicePort capacityServicePort;
    private final ICapacityMapper capacityMapper;

    public Mono<ServerResponse> createCapacity(ServerRequest request) {
        return request.bodyToMono(CapacityDTO.class)
                .flatMap(dto -> {
                    return capacityServicePort.registerCapacityWithTechnologies(
                            capacityMapper.capacityDTOToTechnology(dto),
                            dto.getTechnologyIds());
                })
                .flatMap(msg -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(msg))
                .doOnError(ex -> log.error(CAPACITY_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred", ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    public Mono<ServerResponse> listCapacities(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        String direction = request.queryParam("direction").orElse("asc");

        return capacityServicePort.listCapacitiesPaginated(page, size, sortBy, direction)
                .collectList()
                .flatMap(list -> ServerResponse.ok().bodyValue(list));
    }



    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus,  TechnicalMessage error,
                                                    List<ErrorDTO> errors) {
        return Mono.defer(() -> {
            APIResponse apiErrorResponse = APIResponse
                    .builder()
                    .code(error.getCode())
                    .message(error.getMessage())
                    .date(Instant.now().toString())
                    .errors(errors)
                    .build();
            return ServerResponse.status(httpStatus)
                    .bodyValue(apiErrorResponse);
        });
    }
}