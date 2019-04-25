package io.watchdog.security.config;

public abstract class BeanIds {
    private static final String PREFIX = "io.watchdog.security.";
    public static final String FORM_LOGIN_SUCCESS_HANDLER = PREFIX + "formLoginSuccessHandler";
    public static final String FORM_LOGIN_FAILURE_HANDLER = PREFIX + "formLoginFailureHandler";
    public static final String FORM_LOGIN_TOKEN_VERIFIER = PREFIX + "formLoginTokenVerifier";
    public static final String FORM_LOGIN_REQUEST_VERIFICATION_TOKEN_SERVICE = PREFIX + "formLoginRequestVerificationTokenService";

    public static final String SMS_CODE_LOGIN_SUCCESS_HANDLER = PREFIX + "smsCodeLoginSuccessHandler";
    public static final String SMS_CODE_LOGIN_FAILURE_HANDLER = PREFIX + "smsCodeLoginFailureHandler";
    public static final String SMS_CODE_SENDER = PREFIX + "smsCodeWriter";
    public static final String SMS_CODE_LOGIN_REQUEST_VERIFICATION_TOKEN_SERVICE = PREFIX + "smsCodeLoginSmsCodeVerificationTokenService";
}
