package io.watchdog.security.web.verification.impl.sms;

import io.watchdog.security.web.verification.VerificationToken;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter @Setter
public class SmsCode extends VerificationToken {

    private String forPhone;

    public SmsCode(String key, Duration expireIn, String forPhone) {
        super(key, expireIn);
        this.forPhone = forPhone;
    }

    public SmsCode(String key, int seconds, String forPhone) {
        super(key, seconds);
        this.forPhone = forPhone;
    }
}
