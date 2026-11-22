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
@Table(name = "savings", indexes = {
        @Index(name = "idx_savings_person", columnList = "person_id"),
        @Index(name = "idx_savings_date", columnList = "saving_date")
})
@Getter
@Setter
@NoArgsConstructor
public class SavingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private PersonEntity person;

    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;

    @Column(name = "saving_date", nullable = false)
    private String savingDate;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private String createdAt;
}
