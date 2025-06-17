package com.capacity.microservice_capacity.infrastructure.entrypoints.dto;

import lombok.Data;

@Data
public class CapacityListQueryParams {
    private int page = 0;
    private int size = 10;
    private String sortBy = "name"; // o "technologyCount"
    private String direction = "asc"; // o "desc"
}