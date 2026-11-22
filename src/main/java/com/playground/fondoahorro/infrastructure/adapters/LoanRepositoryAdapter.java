package com.playground.fondoahorro.infrastructure.adapters;

import com.playground.fondoahorro.domain.entity.Loan;
import com.playground.fondoahorro.domain.enums.LoanStatus;
import com.playground.fondoahorro.domain.outputport.LoanRepository;
import com.playground.fondoahorro.domain.vo.LoanListItem;
import com.playground.fondoahorro.infrastructure.entities.LoanEntity;
import com.playground.fondoahorro.infrastructure.mappers.LoanMapper;
import com.playground.fondoahorro.infrastructure.repository.JpaLoanRepository;
import com.playground.fondoahorro.infrastructure.repository.JpaPersonRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class LoanRepositoryAdapter implements LoanRepository {

    private final JpaLoanRepository repository;
    private final JpaPersonRepository people;

    public LoanRepositoryAdapter(JpaLoanRepository repository, JpaPersonRepository people) {
        this.repository = repository;
        this.people = people;
    }

    @Override
    public Loan insert(Loan loan) {
        LoanEntity entity = LoanMapper.toEntity(loan);
        entity.setPerson(people.getReferenceById(loan.personId()));
        return LoanMapper.toDomain(repository.save(entity));
    }

    @Override
    public void updateBalance(Loan loan) {
        LoanEntity entity = repository.findById(loan.id())
                .orElseThrow(() -> new IllegalArgumentException("No existe el préstamo."));
        entity.setPrincipalBalanceCents(loan.principalBalance().toCents());
        entity.setInterestOwedCents(loan.interestOwed().toCents());
        entity.setPaidAmountCents(loan.paidAmount().toCents());
        entity.setNextAccrualDate(loan.nextAccrualDate().toString());
        entity.setStatus(loan.status().name());
        entity.setUpdatedAt(LocalDateTime.now().withNano(0).toString());
        repository.save(entity);
    }

    @Override
    public Optional<Loan> findById(long id) {
        return repository.findById(id).map(LoanMapper::toDomain);
    }

    @Override
    public List<Loan> findByPerson(long personId) {
        return repository.findByPerson_IdOrderByLoanDateDescIdDesc(personId).stream()
                .map(LoanMapper::toDomain).toList();
    }

    @Override
    public List<LoanListItem> findAll(LoanStatus statusFilter, Long personId) {
        return repository.search(statusFilter == null ? null : statusFilter.name(), personId).stream()
                .map(entity -> new LoanListItem(LoanMapper.toDomain(entity), entity.getPerson().getName()))
                .toList();
    }
}
