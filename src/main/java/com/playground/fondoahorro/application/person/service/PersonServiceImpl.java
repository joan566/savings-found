package com.playground.fondoahorro.application.person.service;

import com.playground.fondoahorro.domain.entity.Person;
import com.playground.fondoahorro.domain.inputport.PersonService;
import com.playground.fondoahorro.domain.outputport.PersonRepository;
import com.playground.fondoahorro.domain.vo.PersonSummary;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;

    public PersonServiceImpl(PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Person createPerson(String name, LocalDate birthDate, String phone) {
        Person person = Person.newPerson(name, birthDate, phone);
        return repository.insert(person);
    }

    @Transactional
    public Person updatePerson(long id, String name, LocalDate birthDate, String phone) {
        Person existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La persona no existe."));
        Person updated = existing.withUpdatedDetails(name, birthDate, phone);
        repository.update(updated);
        return repository.findById(id).orElseThrow();
    }

    @Transactional
    public void setActive(long id, boolean active) {
        repository.findById(id).orElseThrow(() -> new IllegalArgumentException("La persona no existe."));
        repository.setActive(id, active);
    }

    public Optional<Person> findById(long id) {
        return repository.findById(id);
    }

    public List<Person> list(String query, boolean includeInactive) {
        return repository.findAll(query, includeInactive);
    }

    public List<PersonSummary> listWithSummary(String query, boolean includeInactive) {
        return repository.findAllWithSummary(query, includeInactive);
    }

    public PersonSummary getSummary(long id) {
        return repository.getSummary(id);
    }
}
