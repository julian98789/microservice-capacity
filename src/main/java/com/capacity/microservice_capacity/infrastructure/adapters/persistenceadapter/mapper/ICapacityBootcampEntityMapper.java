package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.mapper;

import com.capacity.microservice_capacity.domain.model.CapacityBootcamp;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.entity.CapacityBootcampEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICapacityBootcampEntityMapper {

    CapacityBootcamp toModel(CapacityBootcampEntity entity);
    CapacityBootcampEntity toEntity(CapacityBootcamp model);
}