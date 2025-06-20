package com.capacity.microservice_capacity.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CapacityWithTechnologiesDTO {
    private Long id;
    private String name;
    private String description;
    private Long technologyCount;
    private List<TechnologySummaryDTO> technologies;
}
