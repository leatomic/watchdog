package io.watchdog.autoconfigure.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "watchdog.authentication")
@Getter @Setter
public class AuthenticationProperties implements InitializingBean {

    private String loginPageUrl     = "/login";

    private FormLogin       formLogin      = new FormLogin();
    private SmsCodeLogin    smsCodeLogin   = new SmsCodeLogin();

    @Getter @Setter
    public static class FormLogin {
        private boolean enabled = false;
        private String usernameParameter = "username";
        private String passwordParameter = "password";
        private String processingUrl    = "/authenticate/form";
        private String defaultTargetUrl = "/";
        private String failureUrl       = "/login?error&approach=form-login";
        private Verification verification = new Verification();
        private AttemptsLimit attemptsLimit = new AttemptsLimit();

        @Getter @Setter public static class Verification {
            private String tokenType = "image_code";
            private String tokenParameter = "verification-token";
        }

        @Getter @Setter public static class AttemptsLimit {
            private boolean enabled = true;
            private long warningThreshold   = 1;
            private long maximum            = 5;
            private @DurationUnit(ChronoUnit.MINUTES) Duration howLongWillLoginBeDisabled = Duration.ofMinutes(30);
            private String loginDisabledUrl = "/form-login.disabled";
        }
    }

    @Getter @Setter
    public static class SmsCodeLogin {
        private boolean enabled = false;
        private Verification verification = new Verification();
        private String processingUrl    = "/authenticate/sms_code";
        private String defaultTargetUrl = "/";
        private String failureUrl       = "/login?error&approach=sms_code-login";

        @Getter @Setter public static class Verification {
            // 获取时需要提交的验证码类型
            private String tokenType = "sms_code";
            // 获取时需要额外提交的“发送到哪个手机号码”
            private String mobileParameter = "mobile";
            // 最终提交时将一个参数提交
            private String tokenParameter = "verification-token";
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO 校验各属性不能为null
    }
}

