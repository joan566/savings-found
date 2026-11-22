package com.playground.fondoahorro.infrastructure.mappers;

import com.playground.fondoahorro.domain.entity.LoanPayment;
import com.playground.fondoahorro.domain.enums.PaymentMethod;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.infrastructure.entities.LoanEntity;
import com.playground.fondoahorro.infrastructure.entities.LoanPaymentEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class LoanPaymentMapper {

    private LoanPaymentMapper() {
    }

    public static LoanPayment toDomain(LoanPaymentEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new LoanPayment(entity.getId(), Objects.requireNonNull(entity.getLoan().getId(), "loan.id"),
                Money.ofCents(entity.getAmountCents()), Money.ofCents(entity.getInterestPortionCents()),
                Money.ofCents(entity.getPrincipalPortionCents()), LocalDate.parse(entity.getPaymentDate()),
                PaymentMethod.valueOf(entity.getPaymentMethod()), entity.getNotes(), parseDateTime(entity.getCreatedAt()));
    }

    public static LoanPaymentEntity toEntity(LoanPayment domain) {
        Objects.requireNonNull(domain, "domain");
        LoanPaymentEntity entity = new LoanPaymentEntity();
        entity.setId(domain.id());

        LoanEntity loan = new LoanEntity();
        loan.setId(domain.loanId());
        entity.setLoan(loan);

        entity.setAmountCents(domain.amount().toCents());
        entity.setInterestPortionCents(domain.interestPortion().toCents());
        entity.setPrincipalPortionCents(domain.principalPortion().toCents());
        entity.setPaymentDate(domain.paymentDate().toString());
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
