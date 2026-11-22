package com.playground.fondoahorro.infrastructure.mappers;

import com.playground.fondoahorro.domain.entity.BirthdayGift;
import com.playground.fondoahorro.domain.entity.Loan;
import com.playground.fondoahorro.domain.entity.LoanInterestCharge;
import com.playground.fondoahorro.domain.entity.LoanPayment;
import com.playground.fondoahorro.domain.entity.Movement;
import com.playground.fondoahorro.domain.entity.MovementType;
import com.playground.fondoahorro.domain.entity.Person;
import com.playground.fondoahorro.domain.entity.Saving;
import com.playground.fondoahorro.domain.entity.Settlement;
import com.playground.fondoahorro.domain.enums.Fund;
import com.playground.fondoahorro.domain.enums.LoanInterestChargeStatus;
import com.playground.fondoahorro.domain.enums.LoanStatus;
import com.playground.fondoahorro.domain.enums.MovementKind;
import com.playground.fondoahorro.domain.enums.PaymentMethod;
import com.playground.fondoahorro.domain.vo.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityMappersTest {

    private static final LocalDate DATE = LocalDate.of(2025, 6, 15);
    private static final LocalDateTime TIMESTAMP = LocalDateTime.of(2025, 6, 15, 10, 30, 45);

    @Test
    void personMapperPreservesDomainFields() {
        Person domain = new Person(1L, "Ana", LocalDate.of(1980, 3, 10), "123", true, TIMESTAMP, TIMESTAMP);

        assertEquals(domain, PersonMapper.toDomain(PersonMapper.toEntity(domain)));
    }

    @Test
    void savingMapperPreservesDomainFields() {
        Saving domain = new Saving(2L, 1L, Money.ofCents(12_345), DATE, PaymentMethod.CASH, "aporte", TIMESTAMP);

        assertEquals(domain, SavingMapper.toDomain(SavingMapper.toEntity(domain)));
    }

    @Test
    void loanMapperPreservesDomainFields() {
        Loan domain = new Loan(3L, 1L, Money.ofCents(100_000), 300, Money.ofCents(80_000),
                Money.ofCents(2_000), Money.ofCents(20_000), DATE, DATE.plusMonths(1), LoanStatus.ACTIVE,
                "préstamo", TIMESTAMP, TIMESTAMP);

        assertEquals(domain, LoanMapper.toDomain(LoanMapper.toEntity(domain)));
    }

    @Test
    void loanPaymentMapperPreservesDomainFields() {
        LoanPayment domain = new LoanPayment(4L, 3L, Money.ofCents(15_000), Money.ofCents(2_000),
                Money.ofCents(13_000), DATE, PaymentMethod.TRANSFER, "pago", TIMESTAMP);

        assertEquals(domain, LoanPaymentMapper.toDomain(LoanPaymentMapper.toEntity(domain)));
    }

    @Test
    void loanInterestChargeMapperPreservesDomainFields() {
        LoanInterestCharge domain = new LoanInterestCharge(5L, 3L, DATE, Money.ofCents(80_000),
                Money.ofCents(2_400), Money.ofCents(1_000), LoanInterestChargeStatus.PARTIAL, TIMESTAMP);

        assertEquals(domain, LoanInterestChargeMapper.toDomain(LoanInterestChargeMapper.toEntity(domain)));
    }

    @Test
    void movementMapperPreservesDomainFields() {
        Movement domain = new Movement(6L, 2L, Fund.SAVINGS, PaymentMethod.CASH, MovementKind.INCOME,
                Money.ofCents(12_345), DATE, 1L, "savings", 2L, "aporte", TIMESTAMP);

        assertEquals(domain, MovementMapper.toDomain(MovementMapper.toEntity(domain)));
    }

    @Test
    void movementTypeMapperPreservesDomainFields() {
        MovementType domain = new MovementType(2L, "Ahorro", MovementKind.INCOME, "SAVINGS_CONTRIBUTION",
                true, TIMESTAMP, TIMESTAMP);

        assertEquals(domain, MovementTypeMapper.toDomain(MovementTypeMapper.toEntity(domain)));
    }

    @Test
    void birthdayGiftMapperPreservesDomainFields() {
        BirthdayGift domain = new BirthdayGift(7L, 1L, DATE.getYear(), DATE, Money.ofCents(20_000),
                PaymentMethod.CASH, "regalo", TIMESTAMP);

        assertEquals(domain, BirthdayGiftMapper.toDomain(BirthdayGiftMapper.toEntity(domain)));
    }

    @Test
    void settlementMapperPreservesDomainFields() {
        Settlement domain = new Settlement(8L, 1L, 2025, Money.ofCents(100_000), 300,
                Money.ofCents(3_000), Money.ofCents(103_000), TIMESTAMP);

        assertEquals(domain, SettlementMapper.toDomain(SettlementMapper.toEntity(domain)));
    }
}
