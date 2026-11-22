package com.playground.fondoahorro.infrastructure.repository;

import com.playground.fondoahorro.infrastructure.entities.BirthdayGiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaBirthdayGiftRepository extends JpaRepository<BirthdayGiftEntity, Long> {

    Optional<BirthdayGiftEntity> findByPerson_IdAndYear(long personId, int year);
}
