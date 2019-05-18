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

package io.watchdog.security.config.annotation.web;

import io.watchdog.autoconfigure.security.AuthenticationProperties;
import io.watchdog.autoconfigure.security.VerificationProperties;
import io.watchdog.security.config.BeanIds;
import io.watchdog.security.config.annotation.web.configurers.FormLoginAttemptsLimitConfigurer;
import io.watchdog.security.config.annotation.web.configurers.SmsCodeLoginConfigurer;
import io.watchdog.security.config.annotation.web.configurers.VerificationFiltersConfigurer;
import io.watchdog.security.web.WebAttributes;
import io.watchdog.security.web.authentication.*;
import io.watchdog.security.web.verification.VerificationFailureHandler;
import io.watchdog.security.web.verification.VerificationProvider;
import io.watchdog.security.web.verification.VerificationRequestHandler;
import io.watchdog.security.web.verification.VerificationSuccessHandler;
import io.watchdog.security.web.verification.sms.SmsCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Slf4j
@Getter @Setter
public class WatchdogWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProperties authenticationProperties;
    @Autowired
    private VerificationProperties verificationProperties;


    @Override
    public void configure(WebSecurity web) throws Exception {
        // TODO 只过滤默认提供的页面所引入的静态资源的url
        web.ignoring().antMatchers("/favicon.ico", "/js/**", "/css/**", "/images/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        activateVerification(http, verificationProperties);

        enableFormLoginIfNecessary(http);

        enableSmsCodeLoginIfNecessary(http);

    }










    @Autowired(required = false)
    private List<VerificationRequestHandler> verificationRequestHandlers;
    @Autowired(required = false)
    private List<VerificationProvider> verificationProviders;

    private void activateVerification(HttpSecurity http, VerificationProperties verificationProperties) throws Exception {

        VerificationProperties.Service tokenService = verificationProperties.getService();

        String acquiresVerificationTokenUrl = tokenService.getAcquiresTokenUrl();

        getOrApplyVerificationConfigurer(http)
                .tokenEndpoint()
                .acquiresTokenUrl(acquiresVerificationTokenUrl)
                .tokenTypeParameter(tokenService.getTokenTypeParameter())
                .businessParameter(tokenService.getBusinessParameter())
                .applyRequestHandlers(verificationRequestHandlers).and()
                .processing()
                .addProviders(verificationProviders);

        http.authorizeRequests().antMatchers(acquiresVerificationTokenUrl).permitAll();

    }


    @Autowired(required = false) @Qualifier(BeanIds.SMS_CODE_LOGIN_SUCCESS_HANDLER)
    private AuthenticationSuccessHandler smsCodeLoginSuccessHandler;

    @Autowired(required = false) @Qualifier(BeanIds.SMS_CODE_LOGIN_FAILURE_HANDLER)
    private AuthenticationFailureHandler smsCodeLoginFailureHandler;

    @Autowired(required = false)
    private VerificationRequestHandler<SmsCode> smsCodeLoginVerificationRequestHandler;

    private void enableSmsCodeLoginIfNecessary(HttpSecurity http) throws Exception {
        if (!authenticationProperties.getSmsCodeLogin().isEnabled())
            return;

        AuthenticationProperties.SmsCodeLogin smsCodeLogin = authenticationProperties.getSmsCodeLogin();
        String smsCodeLoginProcessingUrl    = smsCodeLogin.getProcessingUrl();
        String smsCodeLoginDefaultTargetUrl = smsCodeLogin.getDefaultTargetUrl();
        String smsCodeLoginFailureUrl       = smsCodeLogin.getFailureUrl();
        getOrApplySmsCodeLoginConfigurer(http)
                .loginProcessingUrl(smsCodeLoginProcessingUrl)
                .mobilePhoneAttribute(WebAttributes.SMS_CODE_LOGIN_USERNAME_ATTRIBUTE)
                .successHandler(smsCodeLoginSuccessHandler)
                .failureHandler(smsCodeLoginFailureHandler);
        http.authorizeRequests()
                .antMatchers(smsCodeLoginProcessingUrl, smsCodeLoginDefaultTargetUrl, smsCodeLoginFailureUrl)
                .permitAll();

        /*
         * 拦截短信验证码登录请求，校验一并提交的短信验证码，并将校验后取得的手机号码保存至session，供后续直接认证
         **/
        RequestMatcher smsCodeLoginProcessingRequestMatcher   = new AntPathRequestMatcher(smsCodeLoginProcessingUrl, "POST");

        VerificationSuccessHandler<SmsCode> smsCodeLoginSmsCodeVerificationSuccessHandler
                = (request, smsCode) -> {
            String mobile = smsCode.getForPhone();
            request.setAttribute(WebAttributes.SMS_CODE_LOGIN_USERNAME_ATTRIBUTE, mobile);
        };

        VerificationFailureHandler smsCodeLoginSmsCodeVerificationFailureHandler
                = (request, response, exception) ->
                smsCodeLoginFailureHandler.onAuthenticationFailure(
                        request, response,
                        new AuthenticationException(exception.getMessage()){}
                );

        VerificationProvider<SmsCode> smsCodeLoginSmsCodeVerifier = new VerificationProvider<>(
                smsCodeLoginProcessingRequestMatcher,
                smsCodeLogin.getVerification().getTokenParameter(),
                smsCodeLoginVerificationRequestHandler.getService(),
                smsCodeLoginSmsCodeVerificationSuccessHandler,
                smsCodeLoginSmsCodeVerificationFailureHandler
        );

        getOrApplyVerificationConfigurer(http)
                .processing().addProvider(smsCodeLoginSmsCodeVerifier);
    }

    private SmsCodeLoginConfigurer<HttpSecurity> getOrApplySmsCodeLoginConfigurer(HttpSecurity http) throws Exception {
        @SuppressWarnings("unchecked")
        SmsCodeLoginConfigurer<HttpSecurity> configurer = http.getConfigurer(SmsCodeLoginConfigurer.class);
        if (configurer == null) {
            configurer = http.apply(new SmsCodeLoginConfigurer<>());
        }
        return configurer;
    }

    @Autowired(required = false)
    private FormLoginAttemptsLimiter formLoginAttemptsLimiter;

    @Autowired(required = false) @Qualifier(BeanIds.FORM_LOGIN_SUCCESS_HANDLER)
    private AuthenticationSuccessHandler formLoginSuccessHandler;

    @Autowired(required = false) @Qualifier(BeanIds.FORM_LOGIN_FAILURE_HANDLER)
    private AuthenticationFailureHandler formLoginFailureHandler;

    @Autowired
    private VerificationRequestHandler<?> formLoginVerificationRequestHandler;
    private void enableFormLoginIfNecessary(HttpSecurity http) throws Exception {
        if (!authenticationProperties.getFormLogin().isEnabled()) {
            return;
        }

        AuthenticationProperties.FormLogin formLogin = authenticationProperties.getFormLogin();

        String formLoginPageUrl             = authenticationProperties.getLoginPageUrl();
        String formLoginProcessingUrl       = formLogin.getProcessingUrl();
        String formLoginDefaultTargetUrl    = formLogin.getDefaultTargetUrl();
        String formLoginFailureUrl          = formLogin.getFailureUrl();

        AuthenticationSuccessHandler loginSuccessHandler = formLoginSuccessHandler;
        AuthenticationFailureHandler loginFailureHandler = formLoginFailureHandler;

        /* 在进行表单登录的认证过程前，预先检测该请求的来源{IP,Username}是否已在黑名单内，准备提前拒绝此次尝试 */
        AuthenticationProperties.FormLogin.AttemptsLimit attemptsLimit = formLogin.getAttemptsLimit();
        if (attemptsLimit.isEnabled()) {

            String formLoginDisabledUrl = attemptsLimit.getLoginDisabledUrl();
            FormLoginDisabledHandler formLoginDisabledHandler = new RedirectFormLoginDisabledHandler(formLoginDisabledUrl);

            getOrApplyFormLoginAttemptsLimitConfigurer(http)
                    .loginProcessingRequestMatcher(new AntPathRequestMatcher(formLoginProcessingUrl, "POST"))
                    .attemptsLimiter(formLoginAttemptsLimiter)
                    .disableLogin(formLoginDisabledHandler);

            http.authorizeRequests().antMatchers(formLoginDisabledUrl).permitAll();

            loginSuccessHandler = new DelegateFormLoginSuccessHandler(formLoginAttemptsLimiter, loginSuccessHandler);
            loginFailureHandler = new DelegateFormLoginFailureHandler(formLoginAttemptsLimiter, formLoginDisabledHandler, loginFailureHandler);
        }

        /* 拦截登录请求，校验一并提交的验证码参数  */
        final AuthenticationFailureHandler finalFormLoginFailureHandler = loginFailureHandler;
        VerificationProvider<?> formLoginTokenVerifier = new VerificationProvider<>(
                new RequiresVerificationFormLoginRequestMatcher(formLoginProcessingUrl),
                formLogin.getVerification().getTokenParameter(),
                formLoginVerificationRequestHandler.getService(),
                (request, response, exception) -> {
                    AuthenticationException wrapperException = new AuthenticationException(exception.getMessage()){};
                    finalFormLoginFailureHandler.onAuthenticationFailure(request, response, wrapperException);
                }
        );
        getOrApplyVerificationConfigurer(http)
                .processing().addProvider(formLoginTokenVerifier);

        http.formLogin()
                .loginPage(formLoginPageUrl)
                .loginProcessingUrl(formLoginProcessingUrl)
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .usernameParameter(formLogin.getUsernameParameter())
                .passwordParameter(formLogin.getPasswordParameter());
        http.authorizeRequests()
                .antMatchers(formLoginPageUrl, formLoginProcessingUrl, formLoginDefaultTargetUrl, formLoginFailureUrl)
                .permitAll();

    }

    private FormLoginAttemptsLimitConfigurer<HttpSecurity> getOrApplyFormLoginAttemptsLimitConfigurer(HttpSecurity http) throws Exception {
        @SuppressWarnings("unchecked")
        FormLoginAttemptsLimitConfigurer<HttpSecurity> configurer = http.getConfigurer(FormLoginAttemptsLimitConfigurer.class);
        if (configurer == null) {
            configurer = http.apply(new FormLoginAttemptsLimitConfigurer<>());
        }
        return configurer;
    }

    @SuppressWarnings("unchecked")
    private VerificationFiltersConfigurer<HttpSecurity> getOrApplyVerificationConfigurer(HttpSecurity http) throws Exception {
        VerificationFiltersConfigurer<HttpSecurity> configurer = http.getConfigurer(VerificationFiltersConfigurer.class);
        if(configurer == null) {
            configurer = http.apply(new VerificationFiltersConfigurer());
        }
        return configurer;
    }


}
