package com.capacity.microservice_capacity.infrastructure.entrypoints.util;

import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class APIResponse {
    private String code;
    private String message;
    private String identifier;
    private String date;
    private CapacityDTO data;
    private List<ErrorDTO> errors;
}
