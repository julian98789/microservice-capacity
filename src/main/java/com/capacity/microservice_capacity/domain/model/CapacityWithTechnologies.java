package com.capacity.microservice_capacity.domain.model;

import java.util.List;

public record CapacityWithTechnologies(
        Long id,
        String name,
        String description,
        Long technologyCount,
        List<TechnologySummary> technologies
) {}