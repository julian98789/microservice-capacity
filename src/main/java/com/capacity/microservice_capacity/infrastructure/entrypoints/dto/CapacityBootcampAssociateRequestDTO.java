package com.capacity.microservice_capacity.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CapacityBootcampAssociateRequestDTO {
    private List<Long> capacityIds;
    private Long bootcampId;
}
