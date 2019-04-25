package io.watchdog.security.web.authentication;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter @Setter
public abstract class FormLoginAttemptsLimiter {
    private long warningThreshold;
    private long maximum;
    private Duration howLongWillLoginBeDisabled;

    public FormLoginAttemptsLimiter(long warningThreshold, long maximum,
                                    Duration howLongWillLoginBeDisabled) {
        this.warningThreshold = warningThreshold;
        this.maximum = maximum;
        this.howLongWillLoginBeDisabled = howLongWillLoginBeDisabled;
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
        long numberOfFailureTimes = incrementNumberOfFailureTimes(details);
        return this.new Feedback(numberOfFailureTimes);
    }

    /**
     * <p>增加并记录来自指定{IP, Username}的登录请求的失败次数
     * <p>记得重置该记录的过期时间</p>
     * @param details 来自哪个{IP, Username}的登录请求
     * @return 自增后的总共的失败次数
     */
    protected abstract long incrementNumberOfFailureTimes(Object details);





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
