package io.watchdog.security.web.verification.sms;

import io.watchdog.security.web.verification.TokenRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter @Setter
public class SmsCodeTokenRequest extends TokenRequest {

    private String mobilePhone;

    @Builder
    public SmsCodeTokenRequest(int codeLength, Duration codeValidityDuration, String mobilePhone) {
        super(codeLength, codeValidityDuration);
        this.mobilePhone = mobilePhone;
    }
}
