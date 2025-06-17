package com.capacity.microservice_capacity.infrastructure.entrypoints.dto;

import lombok.Data;

import java.util.List;

@Data
public class CapacityBootcampAssociateRequestDTO {
    private List<Long> capacityIds;
    private Long bootcampId;
}
