package com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "capacity_bootcamp")
@Getter
@Setter
@RequiredArgsConstructor
public class CapacityBootcampEntity {

    @Id
    private Long id;

    @Column("capacity_id")
    private Long capacityId;

    @Column("bootcamp_id")
    private Long bootcampId; // <--- no uses bootcampId, usa bootcampId

}
