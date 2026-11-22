package com.playground.fondoahorro.infrastructure.adapters;

import com.playground.fondoahorro.domain.entity.BirthdayGift;
import com.playground.fondoahorro.domain.outputport.BirthdayGiftRepository;
import com.playground.fondoahorro.infrastructure.mappers.BirthdayGiftMapper;
import com.playground.fondoahorro.infrastructure.repository.JpaBirthdayGiftRepository;
import com.playground.fondoahorro.infrastructure.repository.JpaPersonRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BirthdayGiftRepositoryAdapter implements BirthdayGiftRepository {

    private final JpaBirthdayGiftRepository repository;
    private final JpaPersonRepository people;

    public BirthdayGiftRepositoryAdapter(JpaBirthdayGiftRepository repository, JpaPersonRepository people) {
        this.repository = repository;
        this.people = people;
    }

    @Override
    public BirthdayGift insert(BirthdayGift gift) {
        var entity = BirthdayGiftMapper.toEntity(gift);
        entity.setPerson(people.getReferenceById(gift.personId()));
        return BirthdayGiftMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<BirthdayGift> findByPersonAndYear(long personId, int year) {
        return repository.findByPerson_IdAndYear(personId, year).map(BirthdayGiftMapper::toDomain);
    }
}
