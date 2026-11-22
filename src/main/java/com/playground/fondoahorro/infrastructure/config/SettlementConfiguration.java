package com.playground.fondoahorro.infrastructure.config;

import com.playground.fondoahorro.application.settlement.service.SettlementServiceImpl;
import com.playground.fondoahorro.domain.inputport.AppSettingsService;
import com.playground.fondoahorro.domain.inputport.SettlementService;
import com.playground.fondoahorro.domain.outputport.PersonRepository;
import com.playground.fondoahorro.domain.outputport.SavingRepository;
import com.playground.fondoahorro.domain.outputport.SettlementRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SettlementConfiguration {

    @Bean
    public SettlementService settlementService(SettlementRepository settlements, SavingRepository savings,
                                               PersonRepository people, AppSettingsService appSettings) {
        return new SettlementServiceImpl(settlements, savings, people, appSettings);
    }
}
