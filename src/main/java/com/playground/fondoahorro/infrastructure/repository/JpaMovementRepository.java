package com.playground.fondoahorro.infrastructure.repository;

import com.playground.fondoahorro.infrastructure.entities.MovementEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaMovementRepository extends JpaRepository<MovementEntity, Long> {

    @EntityGraph(attributePaths = {"movementType", "person"})
    @Query("""
            select movement from MovementEntity movement
            where (:fromDate is null or movement.movementDate >= :fromDate)
              and (:toDate is null or movement.movementDate <= :toDate)
              and (:kind is null or movement.kind = :kind)
              and (:fund is null or movement.fund = :fund)
              and (:paymentMethod is null or movement.paymentMethod = :paymentMethod)
              and (:personId is null or movement.person.id = :personId)
              and (:movementTypeId is null or movement.movementType.id = :movementTypeId)
            order by movement.movementDate desc, movement.id desc
            """)
    List<MovementEntity> search(
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("kind") String kind,
            @Param("fund") String fund,
            @Param("paymentMethod") String paymentMethod,
            @Param("personId") Long personId,
            @Param("movementTypeId") Long movementTypeId);

    Optional<MovementEntity> findFirstByReferenceTableAndReferenceId(String referenceTable, Long referenceId);

    @Query(value = """
            select fund as fund, payment_method as paymentMethod,
                   sum(case when kind = 'INCOME' then amount_cents else -amount_cents end) as balanceCents
            from movements
            group by fund, payment_method
            """, nativeQuery = true)
    List<FundBalanceProjection> calculateBalances();

    interface FundBalanceProjection {
        String getFund();
        String getPaymentMethod();
        Long getBalanceCents();
    }
}
