package com.maiereni.batch.analysis.financial.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 *
 * @author pmaierean on 2026-10-06
 *
 **/
public class DateUtils {
    public static boolean isWeekend(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
