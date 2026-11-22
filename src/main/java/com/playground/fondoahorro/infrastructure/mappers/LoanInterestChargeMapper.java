package com.playground.fondoahorro.infrastructure.mappers;

import com.playground.fondoahorro.domain.entity.LoanInterestCharge;
import com.playground.fondoahorro.domain.enums.LoanInterestChargeStatus;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.infrastructure.entities.LoanEntity;
import com.playground.fondoahorro.infrastructure.entities.LoanInterestChargeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class LoanInterestChargeMapper {

    private LoanInterestChargeMapper() {
    }

    public static LoanInterestCharge toDomain(LoanInterestChargeEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new LoanInterestCharge(entity.getId(), Objects.requireNonNull(entity.getLoan().getId(), "loan.id"),
                LocalDate.parse(entity.getDueDate()), Money.ofCents(entity.getPrincipalBalanceCents()),
                Money.ofCents(entity.getInterestAmountCents()), Money.ofCents(entity.getPaidAmountCents()),
                LoanInterestChargeStatus.valueOf(entity.getStatus()), parseDateTime(entity.getCreatedAt()));
    }

    public static LoanInterestChargeEntity toEntity(LoanInterestCharge domain) {
        Objects.requireNonNull(domain, "domain");
        LoanInterestChargeEntity entity = new LoanInterestChargeEntity();
        entity.setId(domain.id());

        LoanEntity loan = new LoanEntity();
        loan.setId(domain.loanId());
        entity.setLoan(loan);

        entity.setDueDate(domain.dueDate().toString());
        entity.setPrincipalBalanceCents(domain.principalBalance().toCents());
        entity.setInterestAmountCents(domain.interestAmount().toCents());
        entity.setPaidAmountCents(domain.paidAmount().toCents());
        entity.setStatus(domain.status().name());
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
