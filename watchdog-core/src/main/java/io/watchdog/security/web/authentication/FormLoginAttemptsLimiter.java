package io.watchdog.security.web.authentication;

public abstract class FormLoginAttemptsLimiter {
    protected long warningFailureAttempts;
    protected long maximumFailureAttempts;

    public FormLoginAttemptsLimiter(long warningFailureAttempts, long maximumFailureAttempts) {
        this.warningFailureAttempts = warningFailureAttempts;
        this.maximumFailureAttempts = maximumFailureAttempts;
    }

    public abstract boolean canReach(Object details);

    public abstract boolean reachAndWithoutWarning(Object details);

    public abstract void resetAttempts(Object details);

}
