package io.watchdog.security.config;

public abstract class BeanIds {
    private static final String PREFIX = "io.watchdog.security.";
    public static final String SMS_CODE_SENDER = PREFIX + "smsCodeWriter";
    public static final String FORM_LOGIN_REQUEST_VERIFICATION_TOKEN_SERVICE = PREFIX + "formLoginRequestVerificationTokenService";
    public static final String SMS_CODE_LOGIN_REQUEST_VERIFICATION_TOKEN_SERVICE = PREFIX + "smsCodeLoginSmsCodeVerificationTokenService";
}
