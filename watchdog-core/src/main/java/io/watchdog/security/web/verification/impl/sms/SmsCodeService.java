package io.watchdog.security.web.verification.impl.sms;

import io.watchdog.security.web.verification.GeneralTokenService;
import io.watchdog.security.web.verification.TokenServiceException;
import io.watchdog.security.web.verification.TokenRepository;
import io.watchdog.validation.MobilePhoneValidator;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Map;
import java.util.Random;

@Getter @Setter
public class SmsCodeService extends GeneralTokenService<SmsCode> {

    private MobilePhoneValidator mobilePhoneValidator;
    private String toMobileParameter;

    public SmsCodeService(int codeLength, Duration codeValidityDuration,
                          String toMobileParameter,
                          TokenRepository<SmsCode> tokenRepository,
                          MobilePhoneValidator mobilePhoneValidator
    ) {
        super(codeLength, codeValidityDuration, tokenRepository);
        this.toMobileParameter = toMobileParameter;
        this.mobilePhoneValidator = mobilePhoneValidator;
    }

    @Override
    protected SmsCode createToken(Map<String, String[]> params) {

        String key = generateCodeSeq(getCodeLength());

        String forPhone = obtainToMobile(params);

        return new SmsCode(key, getCodeValidityDuration(), forPhone);

    }

    private String generateCodeSeq(int length) {
        final Random random = new Random();
        char[] seq = new char[length];
        char[] codes = { 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G',
                'h', 'H', 'i', 'j', 'J', 'k', 'K', 'L', 'm', 'M', 'n', 'N', 'p', 'P',
                'q', 'Q', 'r', 'R', 's', 'S', 't', 'T', 'u', 'U', 'v', 'V', 'w', 'W',
                'x', 'X', 'y', 'Y', 'z', 'Z',
                '2', '3', '4', '5', '6', '7', '8', '9' };
        for (int i = 0; i < length; i++) {
            seq[i] = codes[random.nextInt(codes.length)];
        }

        return new String(seq);
    }

    private String obtainToMobile(Map<String, String[]> params) {
        String[] strings = params.get(toMobileParameter);
        if (strings == null || strings.length < 1) {
            throw new TokenServiceException("query parameter phone is not found");
        }
        String forPhone = strings[0];
        if (!mobilePhoneValidator.isValid(forPhone, null)) {
            throw new TokenServiceException("invalid mobile phone, parameter " + toMobileParameter + " : " + forPhone);
        }

        return forPhone;
    }

}
