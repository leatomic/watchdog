package io.watchdog.security.web.authentication;

public abstract class FormLoginAttemptsLimiter {
    protected long warningThreshold;
    protected long maximum;

    public FormLoginAttemptsLimiter(long warningThreshold, long maximum) {
        this.warningThreshold = warningThreshold;
        this.maximum = maximum;
    }


    /**
     * 检查此次来自指定{IP, Username}的登录请求的尝试是否是允许的
     * @return 若之前失败次数已超过允许的最大值则返回false，否则返回true
     */
    public boolean checkAttempt(Object details) {
        return !neverAgain(getNumberOfFailureTimes(details));
    }

    protected abstract long getNumberOfFailureTimes(Object details);





    public Feedback recordFailure(Object details) {
        long numberOfFailureTimes = incrementNumberOfTimes(details);
        return this.new Feedback(numberOfFailureTimes);
    }

    protected abstract long incrementNumberOfTimes(Object details);





    public abstract void clearNumberOfFailureTimes(Object details);




    private boolean warning(long numberOfFailureTimes) {
        return numberOfFailureTimes >= warningThreshold;
    }

    private boolean neverAgain(long numberOfFailureTimes) {
        return numberOfFailureTimes >= maximum;
    }

    public class Feedback {
        private long numberOfFailureTimes;
        public Feedback(long numberOfFailureTimes) {
            this.numberOfFailureTimes = numberOfFailureTimes;
        }
        public boolean warning() {
            return FormLoginAttemptsLimiter.this.warning(numberOfFailureTimes);
        }
        public boolean neverAgain() {
            return FormLoginAttemptsLimiter.this.neverAgain(numberOfFailureTimes);
        }
    }

}
