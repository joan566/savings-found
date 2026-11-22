package com.playground.fondoahorro.infrastructure.config;

import com.playground.fondoahorro.application.dashboard.service.DashboardServiceImpl;
import com.playground.fondoahorro.domain.inputport.BirthdayGiftService;
import com.playground.fondoahorro.domain.inputport.DashboardService;
import com.playground.fondoahorro.domain.inputport.LoanService;
import com.playground.fondoahorro.domain.inputport.MovementService;
import com.playground.fondoahorro.domain.inputport.PersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashboardConfiguration {

    @Bean
    public DashboardService dashboardService(MovementService movements, LoanService loans, PersonService people,
                                             BirthdayGiftService birthdayGifts) {
        return new DashboardServiceImpl(movements, loans, people, birthdayGifts);
    }
}
