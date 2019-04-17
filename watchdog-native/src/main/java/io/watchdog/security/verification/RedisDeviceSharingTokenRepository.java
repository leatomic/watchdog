package io.watchdog.security.verification;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public abstract class RedisDeviceSharingTokenRepository<T extends VerificationToken> implements TokenRepository<T> {

    private static final String deviceIdPrefix = "device-";
    private ValueOperations<String, T> ops;
    private String keyPrefix;

    public RedisDeviceSharingTokenRepository(RedisTemplate<String, T> redisTemplate, String keyPrefix) {
        this.ops = redisTemplate.opsForValue();
        this.keyPrefix = keyPrefix;
    }

    protected abstract String obtainDeviceId();

    @Override
    public T load() {
        return ops.get(getKey());
    }

    @Override
    public void save(T token) {
        long inSeconds = Duration.between(LocalDateTime.now(), token.getExpirationTime()).getSeconds();
        ops.set(getKey(), token, inSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void clear() {
        ops.getOperations().delete(getKey());
    }

    private String getKey() {
        return keyPrefix + ":" + deviceIdPrefix + obtainDeviceId();
    }
}
