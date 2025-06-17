package com.capacity.microservice_capacity.application.config;


import com.capacity.microservice_capacity.domain.api.ICapacityBootcampServicePort;
import com.capacity.microservice_capacity.domain.api.ICapacityServicePort;
import com.capacity.microservice_capacity.domain.spi.ICapacityBootcampPersistencePort;
import com.capacity.microservice_capacity.domain.spi.ICapacityPersistencePort;
import com.capacity.microservice_capacity.domain.spi.ICapacityQueryPort;
import com.capacity.microservice_capacity.domain.spi.ITechnologyAssociationPort;
import com.capacity.microservice_capacity.domain.usecase.CapacityBootcampUseCase;
import com.capacity.microservice_capacity.domain.usecase.CapacityUseCase;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.CapacityBootcampPersistenceAdapter;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.CapacityPersistenceAdapter;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.mapper.ICapacityBootcampEntityMapper;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.mapper.ICapacityEntityMapper;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.repository.ICapacityBootcampRepository;
import com.capacity.microservice_capacity.infrastructure.adapters.persistenceadapter.repository.ICapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final ICapacityRepository capacityRepository;
        private final ICapacityEntityMapper capacityEntityMapper;
        private final ICapacityBootcampRepository capacityBootcampRepository;
        private final ICapacityBootcampEntityMapper capacityBootcampEntityMapper;


        @Bean
        public ICapacityPersistencePort capacityPersistencePort() {
                return new CapacityPersistenceAdapter(capacityRepository, capacityEntityMapper);
        }

        @Bean
        public ICapacityServicePort capacityServicePort(
                ICapacityPersistencePort capacityPersistencePort,
                ITechnologyAssociationPort technologyAssociationPort,
                ICapacityQueryPort capacityQueryPort

        ) {
                return new CapacityUseCase(capacityPersistencePort, technologyAssociationPort,capacityQueryPort );
        }

        @Bean
        public ICapacityBootcampPersistencePort capacityBootcampPersistencePort() {
                return new CapacityBootcampPersistenceAdapter(
                        capacityBootcampRepository,
                        capacityBootcampEntityMapper
                );
        }

        @Bean
        public ICapacityBootcampServicePort capacityBootcampServicePort(
                ICapacityBootcampPersistencePort capacityBootcampPersistencePort,
                ICapacityPersistencePort capacityPersistencePort
        ){
                return new CapacityBootcampUseCase(capacityBootcampPersistencePort,capacityPersistencePort);
        }


}