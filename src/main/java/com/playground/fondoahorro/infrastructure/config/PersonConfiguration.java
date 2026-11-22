package com.playground.fondoahorro.infrastructure.config;

import com.playground.fondoahorro.application.person.service.PersonServiceImpl;
import com.playground.fondoahorro.domain.inputport.PersonService;
import com.playground.fondoahorro.domain.outputport.PersonRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonConfiguration {

    @Bean
    public PersonService personService(PersonRepository repository) {
        return new PersonServiceImpl(repository);
    }
}
