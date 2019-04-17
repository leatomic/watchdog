package io.watchdog.samples.provider.user_center.infrastructure.security.authentication;

import io.watchdog.util.Durations;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 密码过期策略，用于确定新设置的密码将于何时过期
 * 默认ExpirationTime为null是表示永久不会过期
 */
public interface CredentialsExpirationPolicy {

    /**
     * 在账号的密码初始化，或者重置密码后需要设置新密码的到期时间，该策略方法的返回值将作为依据
     * @return 新密码的到期时间
     */
    LocalDateTime newExpirationTimeForCredentialsReset();



    /**
     * 宽松的密码政策，密码是永久有效的，无需定期修改
     */
    class Easy implements CredentialsExpirationPolicy {

        @Override
        public LocalDateTime newExpirationTimeForCredentialsReset() {
            return null;
        }

    }

    /**
     * 严格的密码政策，要求定期地修改密码
     */
    class Strict implements CredentialsExpirationPolicy {

        // 修改密码后，密码的有效期将被重置为该值
        private final Duration credentialsExpiration;

        public Strict(Duration credentialsExpiration) {
            Durations.requiresPositive(credentialsExpiration, "credentialsExpiration");
            this.credentialsExpiration = credentialsExpiration;
        }

        @Override
        public LocalDateTime newExpirationTimeForCredentialsReset() {
            return LocalDateTime.now().plus(credentialsExpiration);
        }
    }

}
