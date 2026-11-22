package com.playground.fondoahorro.domain.inputport;

import com.playground.fondoahorro.domain.vo.DashboardSnapshot;
import com.playground.fondoahorro.domain.enums.DashboardPeriod;

public interface DashboardService {

    DashboardSnapshot getSnapshot(DashboardPeriod period);
}
