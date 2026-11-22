package com.playground.fondoahorro.infrastructure.adapters;

import com.playground.fondoahorro.domain.entity.Saving;
import com.playground.fondoahorro.domain.outputport.SavingRepository;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.domain.vo.SavingListItem;
import com.playground.fondoahorro.infrastructure.entities.SavingEntity;
import com.playground.fondoahorro.infrastructure.mappers.SavingMapper;
import com.playground.fondoahorro.infrastructure.repository.JpaPersonRepository;
import com.playground.fondoahorro.infrastructure.repository.JpaSavingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SavingRepositoryAdapter implements SavingRepository {

    private final JpaSavingRepository repository;
    private final JpaPersonRepository people;

    public SavingRepositoryAdapter(JpaSavingRepository repository, JpaPersonRepository people) {
        this.repository = repository;
        this.people = people;
    }

    @Override
    public Saving insert(Saving saving) {
        return SavingMapper.toDomain(repository.save(withManagedPerson(SavingMapper.toEntity(saving), saving.personId())));
    }

    @Override
    public Optional<Saving> findById(long id) {
        return repository.findById(id).map(SavingMapper::toDomain);
    }

    @Override
    public Saving update(Saving saving) {
        return SavingMapper.toDomain(repository.save(withManagedPerson(SavingMapper.toEntity(saving), saving.personId())));
    }

    @Override
    public List<Saving> findByPerson(long personId) {
        return repository.findByPerson_IdOrderBySavingDateDescIdDesc(personId).stream()
                .map(SavingMapper::toDomain).toList();
    }

    @Override
    public List<SavingListItem> findAll(Long personId) {
        return repository.search(personId).stream()
                .map(entity -> new SavingListItem(SavingMapper.toDomain(entity), entity.getPerson().getName()))
                .toList();
    }

    @Override
    public Money totalForPersonAndYear(long personId, int year) {
        Long total = repository.totalForPersonAndYear(personId, Integer.toString(year));
        return Money.ofCents(total == null ? 0L : total);
    }

    private SavingEntity withManagedPerson(SavingEntity entity, long personId) {
        entity.setPerson(people.getReferenceById(personId));
        return entity;
    }
}
