package com.playground.fondoahorro.infrastructure.repository;

import com.playground.fondoahorro.infrastructure.entities.SettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaSettlementRepository extends JpaRepository<SettlementEntity, Long> {

    Optional<SettlementEntity> findByPerson_IdAndYear(long personId, int year);

    List<SettlementEntity> findByPerson_IdOrderByYearDesc(long personId);
}
