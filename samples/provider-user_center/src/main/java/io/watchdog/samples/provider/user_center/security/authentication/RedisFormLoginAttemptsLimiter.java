package io.watchdog.samples.provider.user_center.security.authentication;

import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import io.watchdog.security.web.authentication.FormLoginDetails;
import io.watchdog.util.Durations;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.lang.NonNull;

import java.util.concurrent.TimeUnit;

public class RedisFormLoginAttemptsLimiter extends FormLoginAttemptsLimiter {
    public static final long DEFAULT_WARNING_FAILURE_ATTEMPTS = 3;
    public static final long DEFAULT_MAXIMUM_FAILURE_ATTEMPTS = 8;
    public static final String DEFAULT_KEY_PREFIX = "form-login:failure-attempts:ip-";

    private RedisTemplate<String, String> redisTemplate;
    private String keyPrefix;

    // ~Constructors
    // ================================================================================================
    public RedisFormLoginAttemptsLimiter(RedisTemplate<String, String> redisTemplate,
                                         String keyPrefix,
                                         long warningFailureAttempts, long maximumFailureAttempts) {
        super(warningFailureAttempts, maximumFailureAttempts);
        this.redisTemplate = redisTemplate;
        this.keyPrefix = keyPrefix;
    }

    public RedisFormLoginAttemptsLimiter(RedisTemplate<String, String> redisTemplate,
                                         long warningFailureAttempts, long maximumFailureAttempts) {
        this(redisTemplate, DEFAULT_KEY_PREFIX, warningFailureAttempts, maximumFailureAttempts);
    }

    public RedisFormLoginAttemptsLimiter(RedisTemplate<String, String> redisTemplate) {
        this(redisTemplate, DEFAULT_KEY_PREFIX, DEFAULT_WARNING_FAILURE_ATTEMPTS, DEFAULT_MAXIMUM_FAILURE_ATTEMPTS);
    }


    // ~
    // =================================================================================================
    @Override
    public boolean canReach(Object details) {
        HashOperations<String, String, Long> hashOps = redisTemplate.opsForHash();
        String key = getKey(details);
        Long failureAttempts = hashOps.get(key, ((FormLoginDetails)details).getUsername());
        return failureAttempts == null || failureAttempts < maximumFailureAttempts;
    }

    @Override
    public boolean reachAndWithoutWarning(Object details) {
        String key = getKey(details);
        long expired = Durations.fromNowToNextLocalTime(6, 0 ,0, 0).getSeconds();

        Long failureAttempts = (Long) redisTemplate.executePipelined(new SessionCallback<Long>() {
            @Override
            public Long execute(@NonNull RedisOperations operations) throws DataAccessException {
                BoundHashOperations<String, String, Long> boundHashOperations = operations.boundHashOps(key);
                Long failureAttempts = boundHashOperations.increment(((FormLoginDetails) details).getUsername(), 1L);
                boundHashOperations.expire(expired, TimeUnit.SECONDS);
                return failureAttempts;
            }
        }).get(0);

        return failureAttempts != null && failureAttempts < warningFailureAttempts;
    }

    @Override
    public void resetAttempts(Object details) {
        String key = getKey(details);
        HashOperations<String, String, Long> hashOps = redisTemplate.opsForHash();
        hashOps.put(key, ((FormLoginDetails)details).getUsername(), 0L);
    }

    private String getKey(Object details) {
        return keyPrefix + ((FormLoginDetails)details).getRemoteIpAddress();
    }

}
