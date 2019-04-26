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

package io.watchdog.samples.provider.user_center.infra.security.verification;

import io.watchdog.security.verification.VerificationToken;
import io.watchdog.security.web.verification.TokenRepository;
import io.watchdog.security.web.verification.VerificationRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public abstract class RedisDeviceSharingTokenRepository<T extends VerificationToken> implements TokenRepository<T> {

    private static final String PREFIX = "verification-token";
    private static final String DEVICE_ID_PREFIX = "device-";
    private ValueOperations<String, T> ops;

    public RedisDeviceSharingTokenRepository(RedisTemplate<String, T> redisTemplate) {
        this.ops = redisTemplate.opsForValue();
    }

    protected abstract String obtainDeviceId();

    @Override
    public T load(VerificationRequest.Type forTokenRequestType) {
        return ops.get(assembleKey(forTokenRequestType));
    }

    @Override
    public void save(VerificationRequest.Type forTokenRequestType, T token) {
        long expiredInSeconds = Duration.between(LocalDateTime.now(), token.getExpirationTime()).getSeconds();
        ops.set(assembleKey(forTokenRequestType), token, expiredInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void remove(VerificationRequest.Type forTokenRequestType, T token) {
        ops.getOperations().delete(assembleKey(forTokenRequestType));
    }

    private String assembleKey(VerificationRequest.Type forTokenRequestType) {
        return PREFIX + ":" + DEVICE_ID_PREFIX + obtainDeviceId()
                + ":" + forTokenRequestType + ":" + forTokenRequestType.getBusiness();
    }
}
