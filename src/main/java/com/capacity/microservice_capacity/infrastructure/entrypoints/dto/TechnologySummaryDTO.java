package com.capacity.microservice_capacity.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TechnologySummaryDTO {
    private Long id;
    private String name;
}