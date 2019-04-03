package io.watchdog.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Durations {

    public static void requiresNullOrPositive(Duration duration, String argumentName) {
        if (duration != null && (duration.isNegative() || duration.isZero())) {
            throw new IllegalArgumentException(argumentName + " must be null or positive");
        }
    }

    public static void requiresPositive(Duration duration, String argumentName) {
        if (duration == null || (duration.isNegative() || duration.isZero())) {
            throw new IllegalArgumentException(argumentName + " must positive");
        }
    }

    public static Duration fromNowToNextLocalTime(int hour, int minute, int second, int nanoOfSecond) {
        LocalDateTime   now             = LocalDateTime.now(),
                        localTimeToday  = LocalDate.now().atTime(hour, minute, second, nanoOfSecond),
                        nextLocalTime   = now.isBefore(localTimeToday) ? localTimeToday : localTimeToday.plusDays(1);
        return Duration.between(now, nextLocalTime);
    }

}
