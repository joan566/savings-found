package com.playground.fondoahorro.domain.outputport;

import java.util.Optional;
import com.playground.fondoahorro.domain.entity.BirthdayGift;

public interface BirthdayGiftRepository {

    BirthdayGift insert(BirthdayGift gift);

    Optional<BirthdayGift> findByPersonAndYear(long personId, int year);
}
