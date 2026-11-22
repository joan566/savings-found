package com.playground.fondoahorro.infrastructure.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loans", indexes = {
        @Index(name = "idx_loans_person", columnList = "person_id"),
        @Index(name = "idx_loans_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private PersonEntity person;

    @Column(name = "principal_amount_cents", nullable = false)
    private Long principalAmountCents;

    @Column(name = "interest_rate_bps", nullable = false)
    private Integer interestRateBps;

    @Column(name = "paid_amount_cents", nullable = false)
    private Long paidAmountCents;

    @Column(name = "loan_date", nullable = false)
    private String loanDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    @Column(name = "principal_balance_cents", nullable = false)
    private Long principalBalanceCents;

    @Column(name = "interest_owed_cents", nullable = false)
    private Long interestOwedCents;

    @Column(name = "next_accrual_date", nullable = false)
    private String nextAccrualDate;
}
