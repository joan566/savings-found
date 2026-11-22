package com.playground.fondoahorro.infrastructure.config;

import com.playground.fondoahorro.application.birthday.service.BirthdayGiftServiceImpl;
import com.playground.fondoahorro.domain.inputport.AppSettingsService;
import com.playground.fondoahorro.domain.inputport.BirthdayGiftService;
import com.playground.fondoahorro.domain.outputport.BirthdayGiftRepository;
import com.playground.fondoahorro.domain.outputport.MovementRepository;
import com.playground.fondoahorro.domain.outputport.MovementTypeRepository;
import com.playground.fondoahorro.domain.outputport.PersonRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BirthdayGiftConfiguration {

    @Bean
    public BirthdayGiftService birthdayGiftService(BirthdayGiftRepository gifts, MovementRepository movements,
                                                   MovementTypeRepository movementTypes, PersonRepository people,
                                                   AppSettingsService appSettings) {
        return new BirthdayGiftServiceImpl(gifts, movements, movementTypes, people, appSettings);
    }
}
