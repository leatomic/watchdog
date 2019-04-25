package io.watchdog.security.web.verification.sms;

import io.watchdog.security.verification.GeneralTokenService;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter @Setter
public class SmsCodeService extends GeneralTokenService<SmsCodeTokenRequest, SmsCode> {

    @Override
    public SmsCode allocate(SmsCodeTokenRequest request) {
        String key = generateCodeSeq(request.getCodeLength());
        return new SmsCode(key, request.getCodeValidityDuration(), request.getMobilePhone());
    }

    private String generateCodeSeq(int length) {
        final Random random = new Random();
        char[] seq = new char[length];
        char[] codes = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        for (int i = 0; i < length; i++) {
            seq[i] = codes[random.nextInt(codes.length)];
        }

        return new String(seq);
    }


}
