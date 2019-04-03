package io.watchdog.security.web.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class FormLoginAttemptsLimiter {
    private long warningFailureAttempts;
    private long maximumFailureAttempts;

    public FormLoginAttemptsLimiter(long warningFailureAttempts, long maximumFailureAttempts) {
        this.warningFailureAttempts = warningFailureAttempts;
        this.maximumFailureAttempts = maximumFailureAttempts;
    }

    public abstract boolean canReach(Object details);

    public abstract boolean reachAndWithoutWarning(Object details);

    public abstract void resetAttempts(Object details);

}
