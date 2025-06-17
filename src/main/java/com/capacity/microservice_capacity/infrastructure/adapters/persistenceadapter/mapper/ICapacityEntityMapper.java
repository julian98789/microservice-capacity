package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.mapper;

import com.capacity.microservice_capacity.domain.model.Capacity;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.entity.CapacityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ICapacityEntityMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Capacity toModel(CapacityEntity entity);
    CapacityEntity toEntity(Capacity model);
}