package com.playground.fondoahorro.domain.inputport;

import com.playground.fondoahorro.domain.entity.Saving;
import com.playground.fondoahorro.domain.enums.PaymentMethod;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.domain.vo.SavingListItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SavingService {

    Saving registerSaving(long personId, Money amount, LocalDate date, PaymentMethod method, String notes);

    Saving updateSaving(long savingId, Money amount, LocalDate date, PaymentMethod method, String notes);

    Optional<Saving> findById(long savingId);

    List<Saving> historyForPerson(long personId);

    List<SavingListItem> list(Long personId);
}
