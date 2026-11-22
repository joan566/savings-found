package com.playground.fondoahorro.domain.enums;

import java.time.LocalDate;
import java.time.YearMonth;

public enum DashboardPeriod {
    LAST_6_MONTHS("Últimos 6 meses", 6),
    LAST_12_MONTHS("Últimos 12 meses", 12),
    CURRENT_YEAR("Año actual", 0);

    private final String label;
    private final int monthCount;

    DashboardPeriod(String label, int monthCount) {
        this.label = label;
        this.monthCount = monthCount;
    }

    public LocalDate startDate(LocalDate today) {
        if (this == CURRENT_YEAR) {
            return LocalDate.of(today.getYear(), 1, 1);
        }
        return YearMonth.from(today).minusMonths(monthCount - 1).atDay(1);
    }

    @Override
    public String toString() {
        return label;
    }
}
