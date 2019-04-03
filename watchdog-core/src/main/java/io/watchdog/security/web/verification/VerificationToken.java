package io.watchdog.security.web.verification;

import io.watchdog.util.Durations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

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


    public VerificationToken(String key, int seconds) {
        this(key, Duration.ofSeconds(seconds));
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
