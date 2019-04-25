package io.watchdog.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    /**
     * 获取现在到下一个指定时刻的时间间隔
     * @param theMoment 到下一个几点几分几秒
     */
    public static Duration fromNowToNextLocalTime(LocalTime theMoment) {
        return Duration.between(LocalDateTime.now(), LocalDateTimes.nextLocalTime(theMoment));
    }

}
