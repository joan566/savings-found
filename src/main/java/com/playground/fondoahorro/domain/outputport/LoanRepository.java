package com.playground.fondoahorro.domain.outputport;

import java.util.List;
import java.util.Optional;
import com.playground.fondoahorro.domain.entity.Loan;
import com.playground.fondoahorro.domain.enums.LoanStatus;
import com.playground.fondoahorro.domain.vo.LoanListItem;

public interface LoanRepository {

    Loan insert(Loan loan);

    /** Persists principalBalance/interestOwed/paidAmount/nextAccrualDate/status after an accrual or a payment. */
    void updateBalance(Loan loan);

    Optional<Loan> findById(long id);

    List<Loan> findByPerson(long personId);

    /** statusFilter null means "Todos". personId null means no filter on person. */
    List<LoanListItem> findAll(LoanStatus statusFilter, Long personId);
}
