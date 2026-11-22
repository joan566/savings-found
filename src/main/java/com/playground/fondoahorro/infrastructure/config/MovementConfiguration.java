package com.playground.fondoahorro.infrastructure.config;

import com.playground.fondoahorro.application.movement.service.MovementServiceImpl;
import com.playground.fondoahorro.application.movement.service.MovementTypeServiceImpl;
import com.playground.fondoahorro.domain.inputport.MovementService;
import com.playground.fondoahorro.domain.inputport.MovementTypeService;
import com.playground.fondoahorro.domain.outputport.MovementRepository;
import com.playground.fondoahorro.domain.outputport.MovementTypeRepository;
import com.playground.fondoahorro.domain.outputport.PersonRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MovementConfiguration {

    @Bean
    public MovementService movementService(MovementRepository movements, MovementTypeRepository movementTypes,
                                           PersonRepository people) {
        return new MovementServiceImpl(movements, movementTypes, people);
    }

    @Bean
    public MovementTypeService movementTypeService(MovementTypeRepository repository) {
        return new MovementTypeServiceImpl(repository);
    }
}
