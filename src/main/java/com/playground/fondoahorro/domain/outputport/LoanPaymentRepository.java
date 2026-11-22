package com.playground.fondoahorro.domain.outputport;

import java.util.List;
import com.playground.fondoahorro.domain.entity.LoanPayment;

public interface LoanPaymentRepository {

    LoanPayment insert(LoanPayment payment);

    List<LoanPayment> findByLoan(long loanId);
}
