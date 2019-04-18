package io.watchdog.util;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class Durations {

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
        return Duration.between(LocalDateTime.now(), LocalDateTimes.nextLocalTime(hour, minute, second, nanoOfSecond));
    }

}
