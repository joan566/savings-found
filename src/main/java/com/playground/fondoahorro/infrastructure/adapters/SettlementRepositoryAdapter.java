package com.playground.fondoahorro.infrastructure.adapters;

import com.playground.fondoahorro.domain.entity.Settlement;
import com.playground.fondoahorro.domain.outputport.SettlementRepository;
import com.playground.fondoahorro.infrastructure.mappers.SettlementMapper;
import com.playground.fondoahorro.infrastructure.repository.JpaPersonRepository;
import com.playground.fondoahorro.infrastructure.repository.JpaSettlementRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SettlementRepositoryAdapter implements SettlementRepository {

    private final JpaSettlementRepository repository;
    private final JpaPersonRepository people;

    public SettlementRepositoryAdapter(JpaSettlementRepository repository, JpaPersonRepository people) {
        this.repository = repository;
        this.people = people;
    }

    @Override
    public Settlement upsert(Settlement settlement) {
        var existing = repository.findByPerson_IdAndYear(settlement.personId(), settlement.year());
        var entity = SettlementMapper.toEntity(settlement);
        entity.setPerson(people.getReferenceById(settlement.personId()));
        existing.ifPresent(saved -> entity.setId(saved.getId()));
        return SettlementMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Settlement> findByPersonAndYear(long personId, int year) {
        return repository.findByPerson_IdAndYear(personId, year).map(SettlementMapper::toDomain);
    }

    @Override
    public List<Settlement> findByPerson(long personId) {
        return repository.findByPerson_IdOrderByYearDesc(personId).stream()
                .map(SettlementMapper::toDomain).toList();
    }
}
