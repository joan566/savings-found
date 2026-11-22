package com.playground.fondoahorro.infrastructure.mappers;

import com.playground.fondoahorro.domain.entity.Loan;
import com.playground.fondoahorro.domain.enums.LoanStatus;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.infrastructure.entities.LoanEntity;
import com.playground.fondoahorro.infrastructure.entities.PersonEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class LoanMapper {

    private LoanMapper() {
    }

    public static Loan toDomain(LoanEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new Loan(entity.getId(), Objects.requireNonNull(entity.getPerson().getId(), "person.id"),
                Money.ofCents(entity.getPrincipalAmountCents()), entity.getInterestRateBps(),
                Money.ofCents(entity.getPrincipalBalanceCents()), Money.ofCents(entity.getInterestOwedCents()),
                Money.ofCents(entity.getPaidAmountCents()), LocalDate.parse(entity.getLoanDate()),
                LocalDate.parse(entity.getNextAccrualDate()), LoanStatus.valueOf(entity.getStatus()), entity.getNotes(),
                parseDateTime(entity.getCreatedAt()), parseDateTime(entity.getUpdatedAt()));
    }

    public static LoanEntity toEntity(Loan domain) {
        Objects.requireNonNull(domain, "domain");
        LoanEntity entity = new LoanEntity();
        entity.setId(domain.id());

        PersonEntity person = new PersonEntity();
        person.setId(domain.personId());
        entity.setPerson(person);

        entity.setPrincipalAmountCents(domain.principalAmount().toCents());
        entity.setInterestRateBps(domain.interestRateBps());
        entity.setPrincipalBalanceCents(domain.principalBalance().toCents());
        entity.setInterestOwedCents(domain.interestOwed().toCents());
        entity.setPaidAmountCents(domain.paidAmount().toCents());
        entity.setLoanDate(domain.loanDate().toString());
        entity.setNextAccrualDate(domain.nextAccrualDate().toString());
        entity.setStatus(domain.status().name());
        entity.setNotes(domain.notes());
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
