package com.capacity.microservice_capacity.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    INVALID_CAPACITY_NAME("400", "Invalid Capacity name. Must not be empty, unique, and max 50 chars.", "name"),
    INVALID_CAPACITY_DESCRIPTION("400", "Invalid Capacity description. Must not be empty and max 90 chars.", "description"),
    CAPACITY_ALREADY_EXISTS("409", "Capacity name already exists.", "name"),
    CAPACITY_CREATED("201", "Capacity created successfully", ""),
    CAPACITY_ASSOCIATION_FAILED ("400", "Failed to associate capacity with technologies", ""),
    INVALID_TECHNOLOGY_LIST("400", "Invalid technology list. Must contain between 3 and 20 unique technology IDs.", ""),
    DUPLICATE_CAPACITY_ID("400", "Duplicate capacityIds in request", "capacityIds"),
    CAPACITY_NOT_FOUND("404", "Capacity does not exist", "capacityId"),
    DUPLICATE_TECHNOLOGY_ID("400", "Duplicate technologyIds in request", "technologyIds"),
    CAPACITY_ALREADY_ASSOCIATED("409", "The capacity is already associated ", "capacityId"),
    CAPABILITY_CAPACITY_LIMIT("400", "Cannot associate: capability would exceed 4 capacity associations", "capabilityId"),
    SAVED_ASSOCIATION("200", "Associations saved successfully", "");

    private final String code;
    private final String message;
    private final String param;
}