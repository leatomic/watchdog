/*
 * Copyright (c) 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
