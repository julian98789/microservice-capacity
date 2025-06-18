package com.capacity.microservice_capacity.infrastructure.entrypoints.mapper;

import com.capacity.microservice_capacity.domain.model.CapacityWithTechnologies;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityWithTechnologiesDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICapacityWithTechnologiesMapper {
    CapacityWithTechnologiesDTO toDTO(CapacityWithTechnologies model);
}