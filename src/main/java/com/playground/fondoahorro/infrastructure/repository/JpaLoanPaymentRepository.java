package com.playground.fondoahorro.infrastructure.repository;

import com.playground.fondoahorro.infrastructure.entities.LoanPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaLoanPaymentRepository extends JpaRepository<LoanPaymentEntity, Long> {

    List<LoanPaymentEntity> findByLoan_IdOrderByPaymentDateDescIdDesc(long loanId);
}
