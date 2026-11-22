package com.playground.fondoahorro.infrastructure.adapters;

import com.playground.fondoahorro.domain.entity.LoanPayment;
import com.playground.fondoahorro.domain.outputport.LoanPaymentRepository;
import com.playground.fondoahorro.infrastructure.mappers.LoanPaymentMapper;
import com.playground.fondoahorro.infrastructure.repository.JpaLoanPaymentRepository;
import com.playground.fondoahorro.infrastructure.repository.JpaLoanRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LoanPaymentRepositoryAdapter implements LoanPaymentRepository {

    private final JpaLoanPaymentRepository repository;
    private final JpaLoanRepository loans;

    public LoanPaymentRepositoryAdapter(JpaLoanPaymentRepository repository, JpaLoanRepository loans) {
        this.repository = repository;
        this.loans = loans;
    }

    @Override
    public LoanPayment insert(LoanPayment payment) {
        var entity = LoanPaymentMapper.toEntity(payment);
        entity.setLoan(loans.getReferenceById(payment.loanId()));
        return LoanPaymentMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<LoanPayment> findByLoan(long loanId) {
        return repository.findByLoan_IdOrderByPaymentDateDescIdDesc(loanId).stream()
                .map(LoanPaymentMapper::toDomain).toList();
    }
}
