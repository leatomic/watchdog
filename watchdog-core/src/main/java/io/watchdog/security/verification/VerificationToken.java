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
