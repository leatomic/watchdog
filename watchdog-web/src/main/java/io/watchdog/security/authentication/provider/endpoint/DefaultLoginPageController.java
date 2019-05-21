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

package io.watchdog.security.authentication.provider.endpoint;

import io.watchdog.security.web.authentication.RequiresVerificationFormLoginRequestMatcher;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * 替代{@link org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter}
 */
@Slf4j
@RestController
@RequestMapping
public class DefaultLoginPageController {

    @GetMapping("${watchdog.authentication.login-page-url:/login}")
    @ResponseBody
    public ResponseEntity toLogin(
            HttpServletRequest request,
            // last login failed
            @RequestParam(required = false) String error, @RequestParam(required = false) String approach,
            // logout succeed just now
            @RequestParam(required = false) String logout) {

        LoginPage loginPage = buildPage(request, error, approach, logout);
        return ResponseEntity.ok(loginPage);
    }


    protected LoginPage buildPage(HttpServletRequest request, String error, String approach, String logout) {
        LoginPage result = new LoginPage();

        if (formLoginEnabled) {
            FormLogin formLogin = new FormLogin();
            formLogin.usernameParameter = formLoginUsernameParameter;
            formLogin.passwordParameter = formLoginPasswordParameter;
            if (requiresFormLoginVerification(request)) {
                formLogin.verification = new FormLogin.Verification();
                formLogin.verification.tokenType = formLoginVerificationTokenType;
                formLogin.verification.tokenParameter = formLoginVerificationTokenParameter;
            }
            formLogin.rememberMeParameter = formLoginRememberMeParameter;
            formLogin.processingUrl = formLoginProcessingUrl;
            result.setFormLogin(formLogin);
        }

        if (smsCodeLoginEnabled) {
            SmsCodeLogin smsCodeLogin = new SmsCodeLogin();
            smsCodeLogin.smsCodeTokenTypeParameter = smsCodeLoginVerificationTokenType;
            smsCodeLogin.mobilePhoneParameter = smsCodeLoginVerificationMobilePhoneParameter;
            smsCodeLogin.smsCodeParameter = smsCodeLoginVerificationTokenParameter;
            smsCodeLogin.processingUrl = smsCodeLoginProcessingUrl;
            result.setSmsCodeLogin(smsCodeLogin);
        }

        if (error != null) {    // last login failed
            Error e = new Error();
            e.authenticationApproach = approach;
            e.message = getErrorMessage(request);
        }
        else if (logout != null) {  // logout succeed just now
            result.setLogoutSuccess(true);
        }

        result.setHiddenInputs(resolveHiddenInputs.apply(request));

        return result;
    }

    private boolean requiresFormLoginVerification(HttpServletRequest request) {
        return RequiresVerificationFormLoginRequestMatcher.requiresVerification(request);
    }

    private String getErrorMessage(HttpServletRequest request) {
        String exceptionAttrName = WebAttributes.AUTHENTICATION_EXCEPTION;

        // Assuming forward to this page after login failed
        AuthenticationException ex = (AuthenticationException) request.getAttribute(exceptionAttrName);

        if (ex == null) {
            // Redirection, try to obtain exception from session
            HttpSession session = request.getSession(false);
            if (session != null) {
                ex = (AuthenticationException) session.getAttribute(exceptionAttrName);
            }
        }

        return ex == null ? "none" : ex.getLocalizedMessage();
    }




    // ~ 表单登录相关
    // =================================================================================================================
    @Value("${watchdog.authentication.form-login.enabled}")
    private boolean formLoginEnabled;
    @Value("${watchdog.authentication.form-login.username-parameter}")
    private String formLoginUsernameParameter;
    @Value("${watchdog.authentication.form-login.password-parameter}")
    private String formLoginPasswordParameter;
    @Value("${watchdog.authentication.form-login.verification.token-type}")
    private String formLoginVerificationTokenType;
    @Value("${watchdog.authentication.form-login.verification.token-parameter}")
    private String formLoginVerificationTokenParameter;
    @Value("${watchdog.authentication.form-login.remember-me-parameter}")
    private String formLoginRememberMeParameter;
    @Value("${watchdog.authentication.form-login.processing-url}")
    private String formLoginProcessingUrl;


    // ~ 短信验证码登录相关
    // =================================================================================================================
    @Value("${watchdog.authentication.sms-code-login.enabled}")
    private boolean smsCodeLoginEnabled;
    @Value("${watchdog.authentication.sms-code-login.verification.token-type}")
    private String smsCodeLoginVerificationTokenType;
    @Value("${watchdog.authentication.sms-code-login.verification.mobile-phone-parameter}")
    private String smsCodeLoginVerificationMobilePhoneParameter;
    @Value("${watchdog.authentication.sms-code-login.verification.token-parameter}")
    private String smsCodeLoginVerificationTokenParameter;
    @Value("${watchdog.authentication.sms-code-login.processing-url}")
    private String smsCodeLoginProcessingUrl;


    // ~ CsrfToken等隐藏域的表单项映射相关
    // =================================================================================================================
    private Function<HttpServletRequest, Map<String, String>> resolveHiddenInputs = request -> {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if(token == null) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap(token.getParameterName(), token.getToken());
    };



    @Getter @Setter
    private static class LoginPage {
        private FormLogin       formLogin;
        private SmsCodeLogin    smsCodeLogin;
        private Error error;
        private boolean logoutSuccess;
        private Map<String, String> hiddenInputs;
    }

    @Getter @Setter
    private static class FormLogin {
        private String usernameParameter;
        private String passwordParameter;
        private Verification verification;
        private String rememberMeParameter;
        private String processingUrl;
        @Getter @Setter
        private static class Verification {
            private String tokenType;
            private String tokenParameter;
        }
    }

    @Getter @Setter
    private static class SmsCodeLogin {
        private String smsCodeTokenTypeParameter;
        private String mobilePhoneParameter;
        private String smsCodeParameter;
        private String processingUrl;
    }

    @Getter @Setter
    private static class Error {
        private String authenticationApproach;
        private String message;
    }


////    private boolean openIdLoginEnabled;
////    private String  openIdUsernameParameter;
////    private String  openIdRememberMeParameter;
////    private String  openIdAuthenticationUrl;
////
////    private boolean socialLoginEnabled;
////    private Map<String, String> oauth2AuthorizationUrlToClientName;

}
