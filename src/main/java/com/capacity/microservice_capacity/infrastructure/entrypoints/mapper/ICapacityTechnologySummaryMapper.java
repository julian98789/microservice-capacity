package com.capacity.microservice_capacity.infrastructure.entrypoints.mapper;

import com.capacity.microservice_capacity.domain.model.CapacityTechnologySummary;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityTechnologySummaryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICapacityTechnologySummaryMapper {
    CapacityTechnologySummaryDTO toDTO(CapacityTechnologySummary model);
}