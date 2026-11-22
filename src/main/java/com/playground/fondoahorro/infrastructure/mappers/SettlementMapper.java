package com.playground.fondoahorro.infrastructure.mappers;

import com.playground.fondoahorro.domain.entity.Settlement;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.infrastructure.entities.PersonEntity;
import com.playground.fondoahorro.infrastructure.entities.SettlementEntity;

import java.time.LocalDateTime;
import java.util.Objects;

public final class SettlementMapper {

    private SettlementMapper() {
    }

    public static Settlement toDomain(SettlementEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new Settlement(entity.getId(), Objects.requireNonNull(entity.getPerson().getId(), "person.id"),
                entity.getYear(), Money.ofCents(entity.getSavingsTotalCents()), entity.getInterestRateBps(),
                Money.ofCents(entity.getInterestAmountCents()), Money.ofCents(entity.getTotalAmountCents()),
                parseDateTime(entity.getPreparedAt()));
    }

    public static SettlementEntity toEntity(Settlement domain) {
        Objects.requireNonNull(domain, "domain");
        SettlementEntity entity = new SettlementEntity();
        entity.setId(domain.id());

        PersonEntity person = new PersonEntity();
        person.setId(domain.personId());
        entity.setPerson(person);

        entity.setYear(domain.year());
        entity.setSavingsTotalCents(domain.savingsTotal().toCents());
        entity.setInterestRateBps(domain.interestRateBps());
        entity.setInterestAmountCents(domain.interestAmount().toCents());
        entity.setTotalAmountCents(domain.totalAmount().toCents());
        entity.setPreparedAt(formatDateTime(domain.preparedAt()));
        return entity;
    }

    private static LocalDateTime parseDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }

    private static String formatDateTime(LocalDateTime value) {
        return value == null ? LocalDateTime.now().withNano(0).toString() : value.toString();
    }
}
