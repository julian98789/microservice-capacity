package com.capacity.microservice_capacity.infrastructure.entrypoints.mapper;

import com.capacity.microservice_capacity.domain.model.Capacity;
import com.capacity.microservice_capacity.infrastructure.entrypoints.dto.CapacityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ICapacityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Capacity capacityDTOToTechnology(CapacityDTO capacityDTO);

    CapacityDTO capacityToDTO(Capacity capacity);
}