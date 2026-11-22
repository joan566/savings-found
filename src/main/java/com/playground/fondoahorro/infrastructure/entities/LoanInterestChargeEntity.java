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
@Table(name = "loan_interest_charges", indexes = {
        @Index(name = "idx_loan_interest_charges_loan", columnList = "loan_id"),
        @Index(name = "idx_loan_interest_charges_status", columnList = "loan_id, status")
})
@Getter
@Setter
@NoArgsConstructor
public class LoanInterestChargeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanEntity loan;

    @Column(name = "due_date", nullable = false)
    private String dueDate;

    @Column(name = "principal_balance_cents", nullable = false)
    private Long principalBalanceCents;

    @Column(name = "interest_amount_cents", nullable = false)
    private Long interestAmountCents;

    @Column(name = "paid_amount_cents", nullable = false)
    private Long paidAmountCents;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private String createdAt;
}
