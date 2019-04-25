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
