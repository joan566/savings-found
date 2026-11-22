package com.playground.fondoahorro.infrastructure.adapters;

import com.playground.fondoahorro.domain.entity.LoanInterestCharge;
import com.playground.fondoahorro.domain.enums.LoanInterestChargeStatus;
import com.playground.fondoahorro.domain.outputport.LoanInterestChargeRepository;
import com.playground.fondoahorro.infrastructure.entities.LoanInterestChargeEntity;
import com.playground.fondoahorro.infrastructure.mappers.LoanInterestChargeMapper;
import com.playground.fondoahorro.infrastructure.repository.JpaLoanInterestChargeRepository;
import com.playground.fondoahorro.infrastructure.repository.JpaLoanRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LoanInterestChargeRepositoryAdapter implements LoanInterestChargeRepository {

    private final JpaLoanInterestChargeRepository repository;
    private final JpaLoanRepository loans;

    public LoanInterestChargeRepositoryAdapter(JpaLoanInterestChargeRepository repository, JpaLoanRepository loans) {
        this.repository = repository;
        this.loans = loans;
    }

    @Override
    public LoanInterestCharge insert(LoanInterestCharge charge) {
        LoanInterestChargeEntity entity = LoanInterestChargeMapper.toEntity(charge);
        entity.setLoan(loans.getReferenceById(charge.loanId()));
        return LoanInterestChargeMapper.toDomain(repository.save(entity));
    }

    @Override
    public void updatePayment(LoanInterestCharge charge) {
        LoanInterestChargeEntity entity = repository.findById(charge.id())
                .orElseThrow(() -> new IllegalArgumentException("No existe el cobro de interés."));
        entity.setPaidAmountCents(charge.paidAmount().toCents());
        entity.setStatus(charge.status().name());
        repository.save(entity);
    }

    @Override
    public List<LoanInterestCharge> findUnpaidByLoan(long loanId) {
        return repository.findByLoan_IdAndStatusNotOrderByDueDateAscIdAsc(loanId, LoanInterestChargeStatus.PAID.name())
                .stream().map(LoanInterestChargeMapper::toDomain).toList();
    }

    @Override
    public List<LoanInterestCharge> findByLoan(long loanId) {
        return repository.findByLoan_IdOrderByDueDateDescIdDesc(loanId).stream()
                .map(LoanInterestChargeMapper::toDomain).toList();
    }
}
