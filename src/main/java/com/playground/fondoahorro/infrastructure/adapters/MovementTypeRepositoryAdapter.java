package com.playground.fondoahorro.infrastructure.adapters;

import com.playground.fondoahorro.domain.entity.MovementType;
import com.playground.fondoahorro.domain.enums.MovementKind;
import com.playground.fondoahorro.domain.outputport.MovementTypeRepository;
import com.playground.fondoahorro.infrastructure.entities.MovementTypeEntity;
import com.playground.fondoahorro.infrastructure.mappers.MovementTypeMapper;
import com.playground.fondoahorro.infrastructure.repository.JpaMovementTypeRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MovementTypeRepositoryAdapter implements MovementTypeRepository {

    private final JpaMovementTypeRepository repository;

    public MovementTypeRepositoryAdapter(JpaMovementTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public MovementType insert(MovementType type) {
        return MovementTypeMapper.toDomain(repository.save(MovementTypeMapper.toEntity(type)));
    }

    @Override
    public void rename(long id, String newName) {
        MovementTypeEntity entity = findEntity(id);
        entity.setName(newName);
        entity.setUpdatedAt(now());
        repository.save(entity);
    }

    @Override
    public void setActive(long id, boolean active) {
        MovementTypeEntity entity = findEntity(id);
        entity.setActive(active);
        entity.setUpdatedAt(now());
        repository.save(entity);
    }

    @Override
    public Optional<MovementType> findById(long id) {
        return repository.findById(id).map(MovementTypeMapper::toDomain);
    }

    @Override
    public Optional<MovementType> findByCode(String code) {
        return repository.findByCode(code).map(MovementTypeMapper::toDomain);
    }

    @Override
    public List<MovementType> findAll(boolean includeInactive) {
        List<MovementTypeEntity> entities = includeInactive
                ? repository.findAllByOrderByKindAscNameAsc()
                : repository.findByActiveTrueOrderByKindAscNameAsc();
        return entities.stream().map(MovementTypeMapper::toDomain).toList();
    }

    @Override
    public List<MovementType> findByKind(MovementKind kind, boolean includeInactive) {
        List<MovementTypeEntity> entities = includeInactive
                ? repository.findByKindOrderByNameAsc(kind.name())
                : repository.findByKindAndActiveTrueOrderByNameAsc(kind.name());
        return entities.stream().map(MovementTypeMapper::toDomain).toList();
    }

    private MovementTypeEntity findEntity(long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("No existe el tipo de movimiento."));
    }

    private static String now() {
        return LocalDateTime.now().withNano(0).toString();
    }
}
