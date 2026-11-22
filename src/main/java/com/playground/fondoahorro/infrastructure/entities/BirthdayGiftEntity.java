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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "birthday_gifts",
        uniqueConstraints = @UniqueConstraint(name = "uk_birthday_gifts_person_year", columnNames = {"person_id", "year"}),
        indexes = @Index(name = "idx_birthday_gifts_person", columnList = "person_id"))
@Getter
@Setter
@NoArgsConstructor
public class BirthdayGiftEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private PersonEntity person;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "gift_date", nullable = false)
    private String giftDate;

    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private String createdAt;
}
