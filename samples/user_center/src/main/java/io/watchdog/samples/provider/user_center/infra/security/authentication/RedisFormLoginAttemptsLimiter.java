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

package io.watchdog.samples.provider.user_center.infra.security.authentication;

import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import io.watchdog.security.web.authentication.FormLoginDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RedisFormLoginAttemptsLimiter extends FormLoginAttemptsLimiter {

    private static final String KEY_PREFIX = "form-login:failure-attempts:ip-";

    private RedisTemplate<String, String> redisTemplate;

    // ~Constructors
    // ================================================================================================
    public RedisFormLoginAttemptsLimiter(RedisTemplate<String, String> redisTemplate,
                                         long warningFailureAttempts, long maximumFailureAttempts,
                                         Duration howLongLoginDisabled) {
        super(warningFailureAttempts, maximumFailureAttempts, howLongLoginDisabled);
        this.redisTemplate = redisTemplate;
    }

    // ~
    // =================================================================================================
    @Override
    protected long getNumberOfFailureTimes(Object details) {
        String key = assembleKey(details);
        HashOperations<String, String, Long> hashOps = redisTemplate.opsForHash();
        Long failureAttempts = hashOps.get(key, ((FormLoginDetails)details).getUsername());
        return failureAttempts == null ? 0 : failureAttempts;
    }

    @Override
    protected long incrementNumberOfFailureTimes(Object details) {
        String key = assembleKey(details);
        long expired = getHowLongWillLoginBeDisabled().getSeconds();

        return (Long) redisTemplate.executePipelined(new SessionCallback<Long>() {
            @Override
            public Long execute(@NonNull RedisOperations operations) throws DataAccessException {
                @SuppressWarnings("unchecked")
                BoundHashOperations<String, String, Long> boundHashOperations = operations.boundHashOps(key);
                Long failedAttempts = boundHashOperations.increment(((FormLoginDetails) details).getUsername(), 1L);
                boundHashOperations.expire(expired, TimeUnit.SECONDS);
                return failedAttempts;
            }
        }).get(0);
    }

    @Override
    public void clearNumberOfFailureTimes(Object details) {
        String key = assembleKey(details);
        HashOperations<String, String, Long> hashOps = redisTemplate.opsForHash();
        hashOps.put(key, ((FormLoginDetails)details).getUsername(), 0L);
    }

    private String assembleKey(Object details) {
        return KEY_PREFIX + ((FormLoginDetails)details).getRemoteIpAddress();
    }

}
