package io.watchdog.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class LocalDateTimes {
    public static final LocalDateTime nextLocalTime(int hour, int minute, int second, int nanoOfSecond) {
        LocalDateTime now             = LocalDateTime.now(),
                      localTimeToday  = LocalDate.now().atTime(hour, minute, second, nanoOfSecond);
        return now.isBefore(localTimeToday) ? localTimeToday : localTimeToday.plusDays(1);
    }
}
