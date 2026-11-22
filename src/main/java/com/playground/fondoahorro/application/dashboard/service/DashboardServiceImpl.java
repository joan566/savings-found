package com.playground.fondoahorro.application.dashboard.service;

import com.playground.fondoahorro.domain.inputport.BirthdayGiftService;
import com.playground.fondoahorro.domain.inputport.DashboardService;
import com.playground.fondoahorro.domain.inputport.LoanService;
import com.playground.fondoahorro.domain.inputport.MovementService;
import com.playground.fondoahorro.domain.inputport.PersonService;
import com.playground.fondoahorro.domain.entity.Loan;
import com.playground.fondoahorro.domain.entity.Movement;
import com.playground.fondoahorro.domain.enums.DashboardPeriod;
import com.playground.fondoahorro.domain.enums.LoanStatus;
import com.playground.fondoahorro.domain.enums.MovementKind;
import com.playground.fondoahorro.domain.vo.DashboardSnapshot;
import com.playground.fondoahorro.domain.vo.FundBalances;
import com.playground.fondoahorro.domain.vo.Money;
import com.playground.fondoahorro.domain.vo.MonthlyFundContributions;
import com.playground.fondoahorro.domain.vo.MovementFilter;
import com.playground.fondoahorro.domain.vo.MovementListItem;
import com.playground.fondoahorro.domain.vo.UpcomingBirthday;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardServiceImpl implements DashboardService {

    private static final int UPCOMING_BIRTHDAYS_LIMIT = 5;
    private static final int RECENT_ACTIVITY_LIMIT = 8;

    private final MovementService movementService;
    private final LoanService loanService;
    private final PersonService personService;
    private final BirthdayGiftService birthdayGiftService;

    public DashboardServiceImpl(MovementService movementService, LoanService loanService, PersonService personService,
                             BirthdayGiftService birthdayGiftService) {
        this.movementService = movementService;
        this.loanService = loanService;
        this.personService = personService;
        this.birthdayGiftService = birthdayGiftService;
    }

    public DashboardSnapshot getSnapshot(DashboardPeriod period) {
        FundBalances balances = movementService.getBalances();

        List<Loan> activeLoans = loanService.list(LoanStatus.ACTIVE, null).stream()
                .map(item -> item.loan())
                .toList();
        Money loanedMoney = activeLoans.stream().map(Loan::principalAmount).reduce(Money.ZERO, Money::plus);
        Money outstandingDebt = activeLoans.stream().map(Loan::totalOwed).reduce(Money.ZERO, Money::plus);

        int activePersonCount = personService.list(null, false).size();

        List<UpcomingBirthday> upcomingBirthdays = birthdayGiftService.listUpcoming().stream()
                .limit(UPCOMING_BIRTHDAYS_LIMIT)
                .toList();

        List<MovementListItem> recentActivity = movementService.list(MovementFilter.empty()).stream()
                .limit(RECENT_ACTIVITY_LIMIT)
                .toList();

        List<MonthlyFundContributions> monthlyFundContributions = monthlyFundContributions(period, LocalDate.now());

        return new DashboardSnapshot(balances, loanedMoney, outstandingDebt, activeLoans.size(),
                activePersonCount, upcomingBirthdays, recentActivity, monthlyFundContributions);
    }

    private List<MonthlyFundContributions> monthlyFundContributions(DashboardPeriod period, LocalDate today) {
        LocalDate startDate = period.startDate(today);
        Map<YearMonth, MonthlyFundContributions> monthly = new LinkedHashMap<>();
        for (YearMonth month = YearMonth.from(startDate);
             !month.isAfter(YearMonth.from(today));
             month = month.plusMonths(1)) {
            monthly.put(month, new MonthlyFundContributions(month, Money.ZERO, Money.ZERO));
        }

        MovementFilter filter = new MovementFilter(startDate, today, MovementKind.INCOME,
                null, null, null, null);
        for (MovementListItem item : movementService.list(filter)) {
            Movement movement = item.movement();
            YearMonth month = YearMonth.from(movement.date());
            MonthlyFundContributions current = monthly.get(month);
            if (current == null) {
                continue;
            }

            MonthlyFundContributions updated = switch (movement.fund()) {
                case SAVINGS -> new MonthlyFundContributions(month,
                        current.savings().plus(movement.amount()), current.birthdays());
                case BIRTHDAY -> new MonthlyFundContributions(month,
                        current.savings(), current.birthdays().plus(movement.amount()));
            };
            monthly.put(month, updated);
        }
        return List.copyOf(monthly.values());
    }
}
