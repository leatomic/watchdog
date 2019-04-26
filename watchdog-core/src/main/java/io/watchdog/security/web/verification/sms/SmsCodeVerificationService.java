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

package io.watchdog.security.web.verification.sms;

import io.watchdog.security.verification.TokenService;
import io.watchdog.security.verification.TokenServiceException;
import io.watchdog.security.web.verification.TokenRepository;
import io.watchdog.security.web.verification.VerificationService;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Map;

@Getter @Setter
public class SmsCodeVerificationService extends VerificationService<SmsCodeTokenRequest, SmsCode> {

    public static final String SUPPORTED_TOKEN_TYPE = "sms_code";
    public static final int DEFAULT_CODE_LENGTH = 6;
    public static final Duration DEFAULT_CODE_VALIDITY_DURATION = Duration.ofMinutes(15);


    private String mobilePhoneParameter = "mobile_phone";  // to which mobile phone to send

    public SmsCodeVerificationService(  String forBusiness,
                                        TokenService<SmsCodeTokenRequest, SmsCode> service,
                                        TokenRepository<SmsCode> tokenRepository) {

        super(SUPPORTED_TOKEN_TYPE, forBusiness,
                DEFAULT_CODE_LENGTH, DEFAULT_CODE_VALIDITY_DURATION,
                service, tokenRepository);



    }

    public SmsCodeVerificationService(TokenService<SmsCodeTokenRequest, SmsCode> service,
                                      TokenRepository<SmsCode> tokenRepository) {
        this(DEFAULT_BUSINESS, service, tokenRepository);
    }

    @Override
    protected SmsCodeTokenRequest createRequest(int codeLength, Duration codeValidityDuration, Map<String, String[]> parameterMap) {

        String mobilePhone = obtainMobilePhone(parameterMap);

        return SmsCodeTokenRequest.builder()
                .codeLength(codeLength)
                .codeValidityDuration(codeValidityDuration)
                .mobilePhone(mobilePhone).build();

    }

    private String obtainMobilePhone(Map<String, String[]> params) {
        String[] strings = params.get(mobilePhoneParameter);
        if (strings == null || strings.length < 1) {
            throw new TokenServiceException("query parameter '" + mobilePhoneParameter + "' is not found");
        }
        return strings[0];
    }


}
