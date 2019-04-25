package io.watchdog.security.web.authentication;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class InMemoryFormLoginAttemptsLimiter extends FormLoginAttemptsLimiter {

    private ConcurrentMap<String, Long> detailsFailedAttemptsMap;

    public InMemoryFormLoginAttemptsLimiter(long warningThreshold, long maximum,
                                            Duration howLongWillLoginBeDisabled) {
        super(warningThreshold, maximum, howLongWillLoginBeDisabled);
        detailsFailedAttemptsMap = CacheBuilder.newBuilder()
                                            .expireAfterWrite(howLongWillLoginBeDisabled)
                                            .<String, Long>build().asMap();
    }


    @Override
    protected long getNumberOfFailureTimes(Object details) {
        Long aLong = detailsFailedAttemptsMap.get(assembleKey(details));
        return aLong == null ? 0 : aLong;
    }

    @Override
    protected long incrementNumberOfFailureTimes(Object details) {
        return detailsFailedAttemptsMap.merge(assembleKey(details), 1L, Long::sum);
    }

    @Override
    public void clearNumberOfFailureTimes(Object details) {
        detailsFailedAttemptsMap.remove(assembleKey(details));
    }


    private String assembleKey(Object details) {
        FormLoginDetails details1 = (FormLoginDetails)details;
        return details1.getRemoteIpAddress() + ":" + details1.getUsername();
    }


    // ~ Getters
    // =================================================================================================================
    @Override
    public void setHowLongWillLoginBeDisabled(Duration howLongWillLoginBeDisabled) {
        super.setHowLongWillLoginBeDisabled(howLongWillLoginBeDisabled);
        detailsFailedAttemptsMap = CacheBuilder.newBuilder()
                                        .expireAfterWrite(howLongWillLoginBeDisabled)
                                        .<String, Long>removalListener(
                                            notification -> detailsFailedAttemptsMap.remove(notification.getKey())
                                        )
                                        .build(
                                            CacheLoader.from((String key)-> detailsFailedAttemptsMap.get(key))
                                        )
                                        .asMap();
    }

}
