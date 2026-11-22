package com.playground.fondoahorro.infrastructure.mappers;

import com.playground.fondoahorro.domain.entity.Person;
import com.playground.fondoahorro.infrastructure.entities.PersonEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class PersonMapper {

    private PersonMapper() {
    }

    public static Person toDomain(PersonEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new Person(entity.getId(), entity.getName(), LocalDate.parse(entity.getBirthDate()), entity.getPhone(),
                Objects.requireNonNull(entity.getActive(), "active"), parseDateTime(entity.getCreatedAt()),
                parseDateTime(entity.getUpdatedAt()));
    }

    public static PersonEntity toEntity(Person domain) {
        Objects.requireNonNull(domain, "domain");
        PersonEntity entity = new PersonEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setBirthDate(domain.birthDate().toString());
        entity.setPhone(domain.phone());
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
