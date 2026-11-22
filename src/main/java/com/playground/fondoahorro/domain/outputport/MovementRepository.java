package com.playground.fondoahorro.domain.outputport;

import java.util.List;
import java.util.Optional;
import com.playground.fondoahorro.domain.entity.Movement;
import com.playground.fondoahorro.domain.vo.MovementFilter;
import com.playground.fondoahorro.domain.vo.MovementListItem;
import com.playground.fondoahorro.domain.vo.FundBalances;

public interface MovementRepository {

    /**
     * Inserts a movement alongside the domain entity that produced it.
     */
    Movement insert(Movement movement);

    List<MovementListItem> findAll(MovementFilter filter);

    FundBalances getBalances();

    Optional<Movement> findByReference(String referenceTable, long referenceId);

    void update(Movement movement);
}
