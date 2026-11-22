package com.playground.fondoahorro.infrastructure.mappers;

import com.playground.fondoahorro.domain.entity.BirthdayGift;
import com.playground.fondoahorro.domain.enums.PaymentMethod;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.infrastructure.entities.BirthdayGiftEntity;
import com.playground.fondoahorro.infrastructure.entities.PersonEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class BirthdayGiftMapper {

    private BirthdayGiftMapper() {
    }

    public static BirthdayGift toDomain(BirthdayGiftEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new BirthdayGift(entity.getId(), Objects.requireNonNull(entity.getPerson().getId(), "person.id"),
                entity.getYear(), LocalDate.parse(entity.getGiftDate()), Money.ofCents(entity.getAmountCents()),
                PaymentMethod.valueOf(entity.getPaymentMethod()), entity.getNotes(), parseDateTime(entity.getCreatedAt()));
    }

    public static BirthdayGiftEntity toEntity(BirthdayGift domain) {
        Objects.requireNonNull(domain, "domain");
        BirthdayGiftEntity entity = new BirthdayGiftEntity();
        entity.setId(domain.id());

        PersonEntity person = new PersonEntity();
        person.setId(domain.personId());
        entity.setPerson(person);

        entity.setYear(domain.year());
        entity.setGiftDate(domain.giftDate().toString());
        entity.setAmountCents(domain.amount().toCents());
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
