/*
 * Copyright (c) 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
