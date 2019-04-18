package io.watchdog.security.web.authentication;

import io.watchdog.util.Durations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class InMemoryFormLoginAttemptsLimiter extends FormLoginAttemptsLimiter implements InitializingBean, DisposableBean {

    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private ConcurrentHashMap<String, AtomicLong> failureAttemptsMap = new ConcurrentHashMap<>();

    public InMemoryFormLoginAttemptsLimiter(long warningFailureAttempts, long maximumFailureAttempts) {
        super(warningFailureAttempts, maximumFailureAttempts);
    }


    @Override
    protected long getFailures(Object details) {
        String key = parseKey(details);
        AtomicLong failureAttempts = failureAttemptsMap.get(key);
        return failureAttempts == null ? 0 : failureAttempts.get();
    }

    @Override
    protected long incrementFailure(Object details) {
        String key = parseKey(details);
        AtomicLong failureAttempts = failureAttemptsMap.computeIfAbsent(key, k -> new AtomicLong());
        return failureAttempts.addAndGet(1);
    }

    @Override
    public void clearFailures(Object details) {
        failureAttemptsMap.remove(parseKey(details));
    }







    private String parseKey(Object details) {
        FormLoginDetails details1 = (FormLoginDetails)details;
        return details1.getRemoteIpAddress() + ":" + details1.getUsername();
    }





    @Override
    public void afterPropertiesSet() throws Exception {
        Runnable clearAttemptsTask = () -> {
            log.info("clear InMemoryFormLoginAttemptsLimiter#failureAttemptsMap...");
            failureAttemptsMap.clear();
            log.info("clear InMemoryFormLoginAttemptsLimiter#failureAttemptsMap completed");
        };
        long initialDelay = Durations.fromNowToNextLocalTime(6, 0, 0, 0).getSeconds();
        long period = TimeUnit.DAYS.toSeconds(1);
        service.scheduleAtFixedRate(clearAttemptsTask, initialDelay, period, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() throws Exception {
        log.info("shutting down InMemoryFormLoginAttemptsLimiter#service(ScheduledExecutorService)...");
        service.shutdown();
        failureAttemptsMap.clear();
        service = null;
        log.info("shutdown InMemoryFormLoginAttemptsLimiter#service(ScheduledExecutorService) completed");
    }

    // ~ Getters
    // =================================================================================================================
    public ScheduledExecutorService getService() {
        return service;
    }

    public ConcurrentHashMap<String, AtomicLong> getFailureAttemptsMap() {
        return failureAttemptsMap;
    }


}
