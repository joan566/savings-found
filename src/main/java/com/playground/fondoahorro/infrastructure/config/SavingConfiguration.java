package com.playground.fondoahorro.infrastructure.config;

import com.playground.fondoahorro.application.savings.service.SavingServiceImpl;
import com.playground.fondoahorro.domain.inputport.SavingService;
import com.playground.fondoahorro.domain.outputport.MovementRepository;
import com.playground.fondoahorro.domain.outputport.MovementTypeRepository;
import com.playground.fondoahorro.domain.outputport.PersonRepository;
import com.playground.fondoahorro.domain.outputport.SavingRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SavingConfiguration {

    @Bean
    public SavingService savingService(SavingRepository savings, MovementRepository movements,
                                       MovementTypeRepository movementTypes, PersonRepository people) {
        return new SavingServiceImpl(savings, movements, movementTypes, people);
    }
}
