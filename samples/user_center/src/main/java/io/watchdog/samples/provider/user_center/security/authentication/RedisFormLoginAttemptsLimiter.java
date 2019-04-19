package io.watchdog.samples.provider.user_center.security.authentication;

import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import io.watchdog.security.web.authentication.FormLoginDetails;
import io.watchdog.util.Durations;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.lang.NonNull;

import java.util.concurrent.TimeUnit;

public class RedisFormLoginAttemptsLimiter extends FormLoginAttemptsLimiter {

    private static final String KEY_PREFIX = "form-login:failure-attempts:ip-";

    private RedisTemplate<String, String> redisTemplate;

    // ~Constructors
    // ================================================================================================
    public RedisFormLoginAttemptsLimiter(RedisTemplate<String, String> redisTemplate,
                                         long warningFailureAttempts, long maximumFailureAttempts) {
        super(warningFailureAttempts, maximumFailureAttempts);
        this.redisTemplate = redisTemplate;
    }

    // ~
    // =================================================================================================
    @Override
    protected long getNumberOfFailureTimes(Object details) {
        String key = buildKey(details);
        HashOperations<String, String, Long> hashOps = redisTemplate.opsForHash();
        Long failureAttempts = hashOps.get(key, ((FormLoginDetails)details).getUsername());
        return failureAttempts == null ? 0 : failureAttempts;
    }

    @Override
    protected long incrementNumberOfTimes(Object details) {
        String key = buildKey(details);
        long expired = Durations.fromNowToNextLocalTime(6, 0 ,0, 0).getSeconds();

        return (Long) redisTemplate.executePipelined(new SessionCallback<Long>() {
            @Override
            public Long execute(@NonNull RedisOperations operations) throws DataAccessException {
                @SuppressWarnings("unchecked")
                BoundHashOperations<String, String, Long> boundHashOperations = operations.boundHashOps(key);
                Long failureAttempts1 = boundHashOperations.increment(((FormLoginDetails) details).getUsername(), 1L);
                boundHashOperations.expire(expired, TimeUnit.SECONDS);
                return failureAttempts1;
            }
        }).get(0);
    }

    @Override
    public void clearNumberOfFailureTimes(Object details) {
        String key = buildKey(details);
        HashOperations<String, String, Long> hashOps = redisTemplate.opsForHash();
        hashOps.put(key, ((FormLoginDetails)details).getUsername(), 0L);
    }

    private String buildKey(Object details) {
        return KEY_PREFIX + ((FormLoginDetails)details).getRemoteIpAddress();
    }

}
