package com.playground.fondoahorro.domain.vo;

import java.time.YearMonth;

public record MonthlyFundContributions(YearMonth month, Money savings, Money birthdays) {
}
