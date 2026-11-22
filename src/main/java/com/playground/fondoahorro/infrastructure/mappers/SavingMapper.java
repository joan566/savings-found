package com.playground.fondoahorro.infrastructure.mappers;

import com.playground.fondoahorro.domain.entity.Saving;
import com.playground.fondoahorro.domain.enums.PaymentMethod;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.infrastructure.entities.PersonEntity;
import com.playground.fondoahorro.infrastructure.entities.SavingEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class SavingMapper {

    private SavingMapper() {
    }

    public static Saving toDomain(SavingEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new Saving(entity.getId(), Objects.requireNonNull(entity.getPerson().getId(), "person.id"),
                Money.ofCents(entity.getAmountCents()), LocalDate.parse(entity.getSavingDate()),
                PaymentMethod.valueOf(entity.getPaymentMethod()), entity.getNotes(), parseDateTime(entity.getCreatedAt()));
    }

    public static SavingEntity toEntity(Saving domain) {
        Objects.requireNonNull(domain, "domain");
        SavingEntity entity = new SavingEntity();
        entity.setId(domain.id());

        PersonEntity person = new PersonEntity();
        person.setId(domain.personId());
        entity.setPerson(person);

        entity.setAmountCents(domain.amount().toCents());
        entity.setSavingDate(domain.date().toString());
        entity.setPaymentMethod(domain.paymentMethod().name());
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
