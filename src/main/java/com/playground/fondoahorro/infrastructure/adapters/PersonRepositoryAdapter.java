package com.playground.fondoahorro.infrastructure.adapters;

import com.playground.fondoahorro.domain.entity.Person;
import com.playground.fondoahorro.domain.outputport.PersonRepository;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.domain.vo.PersonSummary;
import com.playground.fondoahorro.infrastructure.entities.PersonEntity;
import com.playground.fondoahorro.infrastructure.mappers.PersonMapper;
import com.playground.fondoahorro.infrastructure.repository.JpaPersonRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PersonRepositoryAdapter implements PersonRepository {

    private final JpaPersonRepository repository;

    public PersonRepositoryAdapter(JpaPersonRepository repository) {
        this.repository = repository;
    }

    @Override
    public Person insert(Person person) {
        return PersonMapper.toDomain(repository.save(PersonMapper.toEntity(person)));
    }

    @Override
    public void update(Person person) {
        PersonEntity entity = PersonMapper.toEntity(person);
        entity.setUpdatedAt(now());
        repository.save(entity);
    }

    @Override
    public void setActive(long id, boolean active) {
        PersonEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la persona."));
        entity.setActive(active);
        entity.setUpdatedAt(now());
        repository.save(entity);
    }

    @Override
    public Optional<Person> findById(long id) {
        return repository.findById(id).map(PersonMapper::toDomain);
    }

    @Override
    public List<Person> findAll(String query, boolean includeInactive) {
        boolean hasQuery = query != null && !query.isBlank();
        List<PersonEntity> entities;
        if (hasQuery) {
            entities = includeInactive
                    ? repository.findByNameContainingIgnoreCaseOrderByNameAsc(query.trim())
                    : repository.findByNameContainingIgnoreCaseAndActiveTrueOrderByNameAsc(query.trim());
        } else {
            entities = includeInactive ? repository.findAllByOrderByNameAsc()
                    : repository.findByActiveTrueOrderByNameAsc();
        }
        return entities.stream().map(PersonMapper::toDomain).toList();
    }

    @Override
    public List<PersonSummary> findAllWithSummary(String query, boolean includeInactive) {
        String normalizedQuery = query == null || query.isBlank() ? null : query.trim();
        return repository.findAllWithSummary(normalizedQuery, includeInactive ? 1 : 0).stream()
                .map(this::toSummary).toList();
    }

    @Override
    public PersonSummary getSummary(long id) {
        return repository.findSummaryById(id).map(this::toSummary)
                .orElseThrow(() -> new IllegalArgumentException("No existe la persona."));
    }

    private PersonSummary toSummary(JpaPersonRepository.PersonSummaryProjection projection) {
        Person person = new Person(projection.getId(), projection.getName(),
                java.time.LocalDate.parse(projection.getBirthDate()), projection.getPhone(),
                Integer.valueOf(1).equals(projection.getActive()), parseDateTime(projection.getCreatedAt()),
                parseDateTime(projection.getUpdatedAt()));
        return new PersonSummary(person, Money.ofCents(projection.getTotalSavingsCents()),
                Money.ofCents(projection.getOutstandingDebtCents()));
    }

    private static String now() {
        return LocalDateTime.now().withNano(0).toString();
    }

    private static LocalDateTime parseDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }
}
