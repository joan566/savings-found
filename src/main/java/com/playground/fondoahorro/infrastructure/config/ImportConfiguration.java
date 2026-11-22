package com.playground.fondoahorro.infrastructure.config;

import com.playground.fondoahorro.application.importing.service.ImportService;
import com.playground.fondoahorro.application.importing.service.ImportServiceImpl;
import com.playground.fondoahorro.domain.inputport.LoanService;
import com.playground.fondoahorro.domain.inputport.PersonService;
import com.playground.fondoahorro.domain.inputport.SavingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportConfiguration {

    @Bean
    public ImportService importService(PersonService people, SavingService savings, LoanService loans) {
        return new ImportServiceImpl(people, savings, loans);
    }
}
