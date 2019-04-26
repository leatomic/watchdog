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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class LocalDateTimes {

    /**
     * 获取从现在起，下一个指定时间来临时的日期时间。
     * @param theMoment 下一个什么时刻，例如下一个凌晨6点
     * @return 如果今天的这个时间点还没来到，则返回今天的这个时间点，否则返回明天的这个时间点
     */
    public static final LocalDateTime nextLocalTime(LocalTime theMoment) {
        LocalDateTime theMomentToday = LocalDate.now().atTime(theMoment);
        return LocalTime.now().isBefore(theMoment) ? theMomentToday : theMomentToday.plusDays(1);
    }

}
