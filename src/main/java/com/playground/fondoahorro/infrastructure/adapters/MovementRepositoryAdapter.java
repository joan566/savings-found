package com.playground.fondoahorro.infrastructure.adapters;

import com.playground.fondoahorro.domain.entity.Movement;
import com.playground.fondoahorro.domain.enums.Fund;
import com.playground.fondoahorro.domain.enums.PaymentMethod;
import com.playground.fondoahorro.domain.outputport.MovementRepository;
import com.playground.fondoahorro.domain.vo.FundBalances;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.domain.vo.MovementFilter;
import com.playground.fondoahorro.domain.vo.MovementListItem;
import com.playground.fondoahorro.infrastructure.entities.MovementEntity;
import com.playground.fondoahorro.infrastructure.mappers.MovementMapper;
import com.playground.fondoahorro.infrastructure.repository.JpaMovementRepository;
import com.playground.fondoahorro.infrastructure.repository.JpaMovementTypeRepository;
import com.playground.fondoahorro.infrastructure.repository.JpaPersonRepository;
import org.springframework.stereotype.Repository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class MovementRepositoryAdapter implements MovementRepository {

    private final JpaMovementRepository repository;
    private final JpaMovementTypeRepository movementTypes;
    private final JpaPersonRepository people;

    public MovementRepositoryAdapter(JpaMovementRepository repository, JpaMovementTypeRepository movementTypes,
                                     JpaPersonRepository people) {
        this.repository = repository;
        this.movementTypes = movementTypes;
        this.people = people;
    }

    @Override
    public Movement insert(Movement movement) {
        return MovementMapper.toDomain(repository.save(withManagedRelations(MovementMapper.toEntity(movement), movement)));
    }

    @Override
    public List<MovementListItem> findAll(MovementFilter filter) {
        return repository.search(date(filter.fromDate()), date(filter.toDate()),
                        filter.kind() == null ? null : filter.kind().name(),
                        filter.fund() == null ? null : filter.fund().name(),
                        filter.paymentMethod() == null ? null : filter.paymentMethod().name(),
                        filter.personId(), filter.movementTypeId())
                .stream()
                .map(entity -> new MovementListItem(MovementMapper.toDomain(entity),
                        entity.getMovementType().getName(),
                        entity.getPerson() == null ? null : entity.getPerson().getName()))
                .toList();
    }

    @Override
    public FundBalances getBalances() {
        Map<Fund, Map<PaymentMethod, Money>> balances = new EnumMap<>(Fund.class);
        for (Fund fund : Fund.values()) {
            Map<PaymentMethod, Money> byMethod = new EnumMap<>(PaymentMethod.class);
            for (PaymentMethod method : PaymentMethod.values()) {
                byMethod.put(method, Money.ZERO);
            }
            balances.put(fund, byMethod);
        }
        repository.calculateBalances().forEach(row -> {
            Fund fund = Fund.valueOf(row.getFund());
            PaymentMethod method = PaymentMethod.valueOf(row.getPaymentMethod());
            balances.get(fund).put(method, Money.ofCents(row.getBalanceCents()));
        });
        return new FundBalances(balances.get(Fund.SAVINGS).get(PaymentMethod.CASH),
                balances.get(Fund.SAVINGS).get(PaymentMethod.TRANSFER),
                balances.get(Fund.BIRTHDAY).get(PaymentMethod.CASH),
                balances.get(Fund.BIRTHDAY).get(PaymentMethod.TRANSFER));
    }

    @Override
    public Optional<Movement> findByReference(String referenceTable, long referenceId) {
        return repository.findFirstByReferenceTableAndReferenceId(referenceTable, referenceId)
                .map(MovementMapper::toDomain);
    }

    @Override
    public void update(Movement movement) {
        MovementEntity entity = repository.findById(movement.id())
                .orElseThrow(() -> new IllegalArgumentException("No existe el movimiento."));
        entity.setPaymentMethod(movement.paymentMethod().name());
        entity.setAmountCents(movement.amount().toCents());
        entity.setMovementDate(movement.date().toString());
        entity.setNotes(movement.notes());
        repository.save(entity);
    }

    private MovementEntity withManagedRelations(MovementEntity entity, Movement movement) {
        entity.setMovementType(movementTypes.getReferenceById(movement.movementTypeId()));
        entity.setPerson(movement.personId() == null ? null : people.getReferenceById(movement.personId()));
        return entity;
    }

    private static String date(java.time.LocalDate value) {
        return value == null ? null : value.toString();
    }
}
