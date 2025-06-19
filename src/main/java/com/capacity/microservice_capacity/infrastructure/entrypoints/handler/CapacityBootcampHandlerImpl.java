package com.capacity.microservice_capacity.infrastructure.entrypoints.handler;

import com.capacity.microservice_capacity.domain.api.ICapacityBootcampServicePort;
import com.capacity.microservice_capacity.domain.enums.TechnicalMessage;
import com.capacity.microservice_capacity.domain.exceptions.BusinessException;
import com.capacity.microservice_capacity.domain.exceptions.TechnicalException;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityBootcampAssociateRequestDTO;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityBootcampCountDTO;
import com.capacity.microservice_capacity.infrastructure.entrypoints.mapper.ICapacityWithTechnologiesMapper;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class CapacityBootcampHandlerImpl {

    private final ICapacityBootcampServicePort service;
    private final ICapacityWithTechnologiesMapper capacityWithTechnologiesMapper;


    public Mono<ServerResponse> associateCapacityBootcamp(ServerRequest request) {
        return request.bodyToMono(CapacityBootcampAssociateRequestDTO.class)
                .flatMap(dto -> service.associateCapabilityToBootcamp(dto.getCapacityIds(), dto.getBootcampId()))
                .flatMap(success -> ServerResponse.ok().bodyValue(TechnicalMessage.SAVED_ASSOCIATION.getMessage()))
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
                .onErrorResume(ex ->
                     buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()))
                );
    }

    public Mono<ServerResponse> getAllBootcampRelationCounts(ServerRequest request) {
        return service.getAllBootcampRelationCounts()
                .map(crc -> new CapacityBootcampCountDTO(crc.bootcampId(), crc.relationCount()))
                .collectList()
                .flatMap(list -> ServerResponse.ok().bodyValue(list));
    }

    public Mono<ServerResponse> getCapacitiesAndTechnologiesByBootcamp(ServerRequest request) {
        Long bootcampId = Long.valueOf(request.queryParam("bootcampId").orElseThrow());
        return service.getCapacitiesWithTechnologiesByBootcamp(bootcampId)
                .map(capacityWithTechnologiesMapper::toDTO)
                .collectList()
                .flatMap(ServerResponse.ok()::bodyValue)
                .onErrorResume(ex ->
                     buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()))
                );
    }

    public Mono<ServerResponse> deleteCapacitiesByBootcampId(ServerRequest request) {
        Long bootcampId = Long.parseLong(request.pathVariable("bootcampId"));
        return service
                .deleteCapacitiesExclusivelyByBootcampId(bootcampId)
                .then(ServerResponse.noContent().build())
                .onErrorResume(ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                .build())
                ));
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
