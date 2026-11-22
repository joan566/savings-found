package com.playground.fondoahorro.domain.inputport;

import com.playground.fondoahorro.domain.entity.Person;
import com.playground.fondoahorro.domain.vo.PersonSummary;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PersonService {

    Person createPerson(String name, LocalDate birthDate, String phone);

    Person updatePerson(long id, String name, LocalDate birthDate, String phone);

    void setActive(long id, boolean active);

    Optional<Person> findById(long id);

    List<Person> list(String query, boolean includeInactive);

    List<PersonSummary> listWithSummary(String query, boolean includeInactive);

    PersonSummary getSummary(long id);
}
