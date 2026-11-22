package com.playground.fondoahorro.infrastructure.repository;

import com.playground.fondoahorro.infrastructure.entities.MovementTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaMovementTypeRepository extends JpaRepository<MovementTypeEntity, Long> {

    Optional<MovementTypeEntity> findByCode(String code);

    List<MovementTypeEntity> findAllByOrderByKindAscNameAsc();

    List<MovementTypeEntity> findByActiveTrueOrderByKindAscNameAsc();

    List<MovementTypeEntity> findByKindOrderByNameAsc(String kind);

    List<MovementTypeEntity> findByKindAndActiveTrueOrderByNameAsc(String kind);
}
