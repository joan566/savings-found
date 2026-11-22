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
@Table(name = "settlements",
        uniqueConstraints = @UniqueConstraint(name = "uk_settlements_person_year", columnNames = {"person_id", "year"}),
        indexes = {
                @Index(name = "idx_settlements_person", columnList = "person_id"),
                @Index(name = "idx_settlements_year", columnList = "year")
        })
@Getter
@Setter
@NoArgsConstructor
public class SettlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private PersonEntity person;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "savings_total_cents", nullable = false)
    private Long savingsTotalCents;

    @Column(name = "interest_rate_bps", nullable = false)
    private Integer interestRateBps;

    @Column(name = "interest_amount_cents", nullable = false)
    private Long interestAmountCents;

    @Column(name = "total_amount_cents", nullable = false)
    private Long totalAmountCents;

    @Column(name = "prepared_at", nullable = false)
    private String preparedAt;
}
