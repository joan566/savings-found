package com.playground.fondoahorro.domain.outputport;

import java.util.List;
import java.util.Optional;
import com.playground.fondoahorro.domain.entity.Person;
import com.playground.fondoahorro.domain.vo.PersonSummary;

public interface PersonRepository {

    Person insert(Person person);

    void update(Person person);

    void setActive(long id, boolean active);

    Optional<Person> findById(long id);

    List<Person> findAll(String query, boolean includeInactive);

    List<PersonSummary> findAllWithSummary(String query, boolean includeInactive);

    PersonSummary getSummary(long id);
}
