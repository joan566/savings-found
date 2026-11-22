package com.playground.fondoahorro.infrastructure.mappers;

import com.playground.fondoahorro.domain.entity.MovementType;
import com.playground.fondoahorro.domain.enums.MovementKind;
import com.playground.fondoahorro.infrastructure.entities.MovementTypeEntity;

import java.time.LocalDateTime;
import java.util.Objects;

public final class MovementTypeMapper {

    private MovementTypeMapper() {
    }

    public static MovementType toDomain(MovementTypeEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new MovementType(entity.getId(), entity.getName(), MovementKind.valueOf(entity.getKind()),
                entity.getCode(), Objects.requireNonNull(entity.getActive(), "active"),
                parseDateTime(entity.getCreatedAt()), parseDateTime(entity.getUpdatedAt()));
    }

    public static MovementTypeEntity toEntity(MovementType domain) {
        Objects.requireNonNull(domain, "domain");
        MovementTypeEntity entity = new MovementTypeEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setKind(domain.kind().name());
        entity.setCode(domain.code());
        entity.setActive(domain.active());
        entity.setCreatedAt(formatDateTime(domain.createdAt()));
        entity.setUpdatedAt(formatDateTime(domain.updatedAt()));
        return entity;
    }

    private static LocalDateTime parseDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }

    private static String formatDateTime(LocalDateTime value) {
        return value == null ? LocalDateTime.now().withNano(0).toString() : value.toString();
    }
}
