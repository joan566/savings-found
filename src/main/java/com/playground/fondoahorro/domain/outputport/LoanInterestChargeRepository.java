package com.playground.fondoahorro.domain.outputport;

import java.util.List;
import com.playground.fondoahorro.domain.entity.LoanInterestCharge;

public interface LoanInterestChargeRepository {

    LoanInterestCharge insert(LoanInterestCharge charge);

    /** Persists paidAmount/status after a payment has been allocated to this charge. */
    void updatePayment(LoanInterestCharge charge);

    /** Unpaid/partially-paid charges for a loan, oldest due date first — used to allocate a payment across them. */
    List<LoanInterestCharge> findUnpaidByLoan(long loanId);

    /** Full charge history for a loan, most recent first — for the loan detail screen. */
    List<LoanInterestCharge> findByLoan(long loanId);
}
