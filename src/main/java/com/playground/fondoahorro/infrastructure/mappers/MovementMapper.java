package com.playground.fondoahorro.infrastructure.mappers;

import com.playground.fondoahorro.domain.entity.Movement;
import com.playground.fondoahorro.domain.enums.Fund;
import com.playground.fondoahorro.domain.enums.MovementKind;
import com.playground.fondoahorro.domain.enums.PaymentMethod;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.infrastructure.entities.MovementEntity;
import com.playground.fondoahorro.infrastructure.entities.MovementTypeEntity;
import com.playground.fondoahorro.infrastructure.entities.PersonEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class MovementMapper {

    private MovementMapper() {
    }

    public static Movement toDomain(MovementEntity entity) {
        Objects.requireNonNull(entity, "entity");
        Long personId = entity.getPerson() == null ? null
                : Objects.requireNonNull(entity.getPerson().getId(), "person.id");
        return new Movement(entity.getId(), Objects.requireNonNull(entity.getMovementType().getId(), "movementType.id"),
                Fund.valueOf(entity.getFund()), PaymentMethod.valueOf(entity.getPaymentMethod()),
                MovementKind.valueOf(entity.getKind()), Money.ofCents(entity.getAmountCents()),
                LocalDate.parse(entity.getMovementDate()), personId, entity.getReferenceTable(), entity.getReferenceId(),
                entity.getNotes(), parseDateTime(entity.getCreatedAt()));
    }

    public static MovementEntity toEntity(Movement domain) {
        Objects.requireNonNull(domain, "domain");
        MovementEntity entity = new MovementEntity();
        entity.setId(domain.id());

        MovementTypeEntity movementType = new MovementTypeEntity();
        movementType.setId(domain.movementTypeId());
        entity.setMovementType(movementType);

        entity.setFund(domain.fund().name());
        entity.setPaymentMethod(domain.paymentMethod().name());
        entity.setKind(domain.kind().name());
        entity.setAmountCents(domain.amount().toCents());
        entity.setMovementDate(domain.date().toString());
        if (domain.personId() != null) {
            PersonEntity person = new PersonEntity();
            person.setId(domain.personId());
            entity.setPerson(person);
        }
        entity.setReferenceTable(domain.referenceTable());
        entity.setReferenceId(domain.referenceId());
        entity.setNotes(domain.notes());
        entity.setCreatedAt(formatDateTime(domain.createdAt()));
        return entity;
    }

    private static LocalDateTime parseDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }

    private static String formatDateTime(LocalDateTime value) {
        return value == null ? LocalDateTime.now().withNano(0).toString() : value.toString();
    }
}
