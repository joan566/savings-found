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
@Table(name = "loan_payments", indexes = {
        @Index(name = "idx_loan_payments_loan", columnList = "loan_id"),
        @Index(name = "idx_loan_payments_date", columnList = "payment_date")
})
@Getter
@Setter
@NoArgsConstructor
public class LoanPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanEntity loan;

    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;

    @Column(name = "payment_date", nullable = false)
    private String paymentDate;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "interest_portion_cents", nullable = false)
    private Long interestPortionCents;

    @Column(name = "principal_portion_cents", nullable = false)
    private Long principalPortionCents;
}
