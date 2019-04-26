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

package io.watchdog.security.web.authentication;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class InMemoryFormLoginAttemptsLimiter extends FormLoginAttemptsLimiter {

    private ConcurrentMap<String, Long> detailsFailedAttemptsMap;

    public InMemoryFormLoginAttemptsLimiter(long warningThreshold, long maximum,
                                            Duration howLongWillLoginBeDisabled) {
        super(warningThreshold, maximum, howLongWillLoginBeDisabled);
        detailsFailedAttemptsMap = CacheBuilder.newBuilder()
                                            .expireAfterWrite(howLongWillLoginBeDisabled)
                                            .<String, Long>build().asMap();
    }


    @Override
    protected long getNumberOfFailureTimes(Object details) {
        Long aLong = detailsFailedAttemptsMap.get(assembleKey(details));
        return aLong == null ? 0 : aLong;
    }

    @Override
    protected long incrementNumberOfFailureTimes(Object details) {
        return detailsFailedAttemptsMap.merge(assembleKey(details), 1L, Long::sum);
    }

    @Override
    public void clearNumberOfFailureTimes(Object details) {
        detailsFailedAttemptsMap.remove(assembleKey(details));
    }


    private String assembleKey(Object details) {
        FormLoginDetails details1 = (FormLoginDetails)details;
        return details1.getRemoteIpAddress() + ":" + details1.getUsername();
    }


    // ~ Getters
    // =================================================================================================================
    @Override
    public void setHowLongWillLoginBeDisabled(Duration howLongWillLoginBeDisabled) {
        super.setHowLongWillLoginBeDisabled(howLongWillLoginBeDisabled);
        detailsFailedAttemptsMap = CacheBuilder.newBuilder()
                                        .expireAfterWrite(howLongWillLoginBeDisabled)
                                        .<String, Long>removalListener(
                                            notification -> detailsFailedAttemptsMap.remove(notification.getKey())
                                        )
                                        .build(
                                            CacheLoader.from((String key)-> detailsFailedAttemptsMap.get(key))
                                        )
                                        .asMap();
    }

}
