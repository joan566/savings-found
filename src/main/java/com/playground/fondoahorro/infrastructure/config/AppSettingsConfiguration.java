package com.playground.fondoahorro.infrastructure.config;

import com.playground.fondoahorro.application.settings.service.AppSettingsServiceImpl;
import com.playground.fondoahorro.domain.inputport.AppSettingsService;
import com.playground.fondoahorro.domain.outputport.AppSettingsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppSettingsConfiguration {

    @Bean
    public AppSettingsService appSettingsService(AppSettingsRepository repository) {
        return new AppSettingsServiceImpl(repository);
    }
}
