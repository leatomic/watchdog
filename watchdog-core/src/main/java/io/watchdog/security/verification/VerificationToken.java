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

package io.watchdog.security.verification;

import io.watchdog.util.Durations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>验证码的抽象类
 * <p>它表明每个验证码对象都应该有用于匹配比较的token串:key，且在一定时间内有效（expirationTime表示什么时候失效）</p>
 */
public abstract class VerificationToken {

    private final String key;
    private final LocalDateTime expirationTime;

    // ~Constructors
    // ====================================================================
    public VerificationToken(String key, Duration expireIn) {
        this.key = Objects.requireNonNull(key);

        Durations.requiresPositive(expireIn, "expireIn");
        this.expirationTime = LocalDateTime.now().plus(expireIn);
    }

    // ====================================================================
    public String getKey() {
        return key;
    }


    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public boolean isExpired() {
        return expirationTime.isBefore(LocalDateTime.now());
    }

}
