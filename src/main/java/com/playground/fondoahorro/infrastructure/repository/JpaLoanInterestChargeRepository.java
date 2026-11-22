package com.playground.fondoahorro.infrastructure.repository;

import com.playground.fondoahorro.infrastructure.entities.LoanInterestChargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaLoanInterestChargeRepository extends JpaRepository<LoanInterestChargeEntity, Long> {

    List<LoanInterestChargeEntity> findByLoan_IdAndStatusNotOrderByDueDateAscIdAsc(long loanId, String status);

    List<LoanInterestChargeEntity> findByLoan_IdOrderByDueDateDescIdDesc(long loanId);
}
