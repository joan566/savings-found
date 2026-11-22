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
@Table(name = "movements", indexes = {
        @Index(name = "idx_movements_date", columnList = "movement_date"),
        @Index(name = "idx_movements_fund_method_kind", columnList = "fund, payment_method, kind"),
        @Index(name = "idx_movements_person", columnList = "person_id"),
        @Index(name = "idx_movements_type", columnList = "movement_type_id"),
        @Index(name = "idx_movements_reference", columnList = "reference_table, reference_id")
})
@Getter
@Setter
@NoArgsConstructor
public class MovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movement_type_id", nullable = false)
    private MovementTypeEntity movementType;

    @Column(name = "fund", nullable = false)
    private String fund;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "kind", nullable = false)
    private String kind;

    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;

    @Column(name = "movement_date", nullable = false)
    private String movementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private PersonEntity person;

    @Column(name = "reference_table")
    private String referenceTable;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private String createdAt;
}
