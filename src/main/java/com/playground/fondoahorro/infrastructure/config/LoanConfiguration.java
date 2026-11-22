package com.playground.fondoahorro.infrastructure.config;

import com.playground.fondoahorro.application.loan.service.LoanServiceImpl;
import com.playground.fondoahorro.domain.inputport.AppSettingsService;
import com.playground.fondoahorro.domain.inputport.LoanService;
import com.playground.fondoahorro.domain.outputport.LoanInterestChargeRepository;
import com.playground.fondoahorro.domain.outputport.LoanPaymentRepository;
import com.playground.fondoahorro.domain.outputport.LoanRepository;
import com.playground.fondoahorro.domain.outputport.MovementRepository;
import com.playground.fondoahorro.domain.outputport.MovementTypeRepository;
import com.playground.fondoahorro.domain.outputport.PersonRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanConfiguration {

    @Bean
    public LoanService loanService(LoanRepository loans, LoanPaymentRepository payments,
                                   LoanInterestChargeRepository interestCharges, MovementRepository movements,
                                   MovementTypeRepository movementTypes, PersonRepository people,
                                   AppSettingsService appSettings) {
        return new LoanServiceImpl(loans, payments, interestCharges, movements, movementTypes, people, appSettings);
    }
}
