package com.playground.fondoahorro.infrastructure.repository;

import com.playground.fondoahorro.infrastructure.entities.LoanEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaLoanRepository extends JpaRepository<LoanEntity, Long> {

    List<LoanEntity> findByPerson_IdOrderByLoanDateDescIdDesc(long personId);

    @EntityGraph(attributePaths = "person")
    @Query("""
            select loan from LoanEntity loan
            where (:status is null or loan.status = :status)
              and (:personId is null or loan.person.id = :personId)
            order by loan.loanDate desc, loan.id desc
            """)
    List<LoanEntity> search(@Param("status") String status, @Param("personId") Long personId);
}
