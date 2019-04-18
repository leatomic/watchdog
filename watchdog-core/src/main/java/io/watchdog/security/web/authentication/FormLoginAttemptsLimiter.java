package io.watchdog.security.web.authentication;

public abstract class FormLoginAttemptsLimiter {
    protected long warningFailureAttempts;
    protected long maximumFailureAttempts;

    public FormLoginAttemptsLimiter(long warningFailureAttempts, long maximumFailureAttempts) {
        this.warningFailureAttempts = warningFailureAttempts;
        this.maximumFailureAttempts = maximumFailureAttempts;
    }




    public boolean checkAttempt(Object details) {
        return !neverAgain(getFailures(details));
    }

    protected abstract long getFailures(Object details);





    public Feedback recordFailure(Object details) {
        long failureAttempts = incrementFailure(details);
        return this.new Feedback(failureAttempts);
    }

    protected abstract long incrementFailure(Object details);





    public abstract void clearFailures(Object details);




    private boolean warning(long failureAttempts) {
        return failureAttempts >= warningFailureAttempts;
    }

    private boolean neverAgain(long failureAttempts) {
        return failureAttempts >= maximumFailureAttempts;
    }

    public class Feedback {
        private long failureAttempts;
        public Feedback(long failureAttempts) {
            this.failureAttempts = failureAttempts;
        }
        public boolean warning() {
            return FormLoginAttemptsLimiter.this.warning(failureAttempts);
        }
        public boolean neverAgain() {
            return FormLoginAttemptsLimiter.this.neverAgain(failureAttempts);
        }
    }

}
