package io.watchdog.security.web.authentication;

import io.watchdog.util.Durations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisFormLoginAttemptsLimiter extends FormLoginAttemptsLimiter {
    public static final long DEFAULT_WARNING_FAILURE_ATTEMPTS = 1;
    public static final long DEFAULT_MAXIMUM_FAILURE_ATTEMPTS = 8;
    public static final String DEFAULT_KEY_PREFIX = "formLogin-login:failure-attempts:ip-";

    private HashOperations<String, String, Long> hashOps;
    private String keyPrefix;

    // ~Constructors
    // ================================================================================================
    public RedisFormLoginAttemptsLimiter(RedisTemplate<String, Long> longRedisTemplate,
                                         String keyPrefix,
                                         long warningFailureAttempts, long maximumFailureAttempts) {
        super(warningFailureAttempts, maximumFailureAttempts);
        this.hashOps = longRedisTemplate.opsForHash();
        this.keyPrefix = keyPrefix;
    }

    public RedisFormLoginAttemptsLimiter(RedisTemplate<String, Long> longRedisTemplate,
                                         long warningFailureAttempts, long maximumFailureAttempts) {
        this(longRedisTemplate, DEFAULT_KEY_PREFIX, warningFailureAttempts, maximumFailureAttempts);
    }

    public RedisFormLoginAttemptsLimiter(RedisTemplate<String, Long> longRedisTemplate) {
        this(longRedisTemplate, DEFAULT_KEY_PREFIX, DEFAULT_WARNING_FAILURE_ATTEMPTS, DEFAULT_MAXIMUM_FAILURE_ATTEMPTS);
    }


    // ~
    // =================================================================================================
    @Override
    public boolean canReach(Object details) {
        String key = getKey(details);
        Long failureAttempts = hashOps.get(key, ((FormLoginDetails)details).getUsername());
        return failureAttempts == null || failureAttempts < getMaximumFailureAttempts();
    }

    @Override
    public boolean reachAndWithoutWarning(Object details) {
        String key = getKey(details);
        long failureAttempts = hashOps.increment(key, ((FormLoginDetails)details).getUsername(), 1L);

        // 无论是否是第一次创建该key对应的项都重置一下其过期时间（到隔天早上6点）
        long expired = Durations.fromNowToNextLocalTime(6, 0 ,0, 0).getSeconds();
        hashOps.getOperations().expire(key, expired, TimeUnit.SECONDS);

        return failureAttempts < getWarningFailureAttempts();
    }

    @Override
    public void resetAttempts(Object details) {
        String key = getKey(details);
        hashOps.put(key, ((FormLoginDetails)details).getUsername(), 0L);
    }

    private String getKey(Object details) {
        return keyPrefix + ((FormLoginDetails)details).getRemoteIpAddress();
    }

}
