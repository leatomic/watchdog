package io.watchdog.security.config.annotation.web;

import io.watchdog.security.config.annotation.web.configurers.FormLoginAttemptsLimitConfigurer;
import io.watchdog.security.config.annotation.web.configurers.SmsCodeLoginConfigurer;
import io.watchdog.security.config.annotation.web.configurers.VerificationFiltersConfigurer;
import io.watchdog.security.verification.TokenService;
import io.watchdog.security.web.WebAttributes;
import io.watchdog.security.web.authentication.*;
import io.watchdog.security.web.verification.VerificationFailureHandler;
import io.watchdog.security.web.verification.VerificationProvider;
import io.watchdog.security.web.verification.VerificationSuccessHandler;
import io.watchdog.security.web.verification.sms.SmsCode;
import io.watchdog.security.web.verification.sms.SmsCodeService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Order(99)
@Getter @Setter
public class CoreWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private String formLoginProcessingUrl;
    private String formLoginDefaultTargetUrl;
    private AuthenticationSuccessHandler formLoginSuccessHandler;
    private String formLoginFailureUrl;
    private AuthenticationFailureHandler formLoginFailureHandler;
    private FormLoginAttemptsLimiter formLoginAttemptsLimiter;
    private FormLoginAttemptsLimitHandler formLoginAttemptsLimitHandler;

    private String smsCodeLoginProcessingUrl;
    private String smsCodeLoginDefaultTargetUrl;
    private AuthenticationSuccessHandler smsCodeLoginSuccessHandler;
    private String smsCodeLoginFailureUrl;
    private AuthenticationFailureHandler smsCodeLoginFailureHandler;

    private String verificationServiceAcquiresTokenUrl;
    private VerificationFiltersConfigurer<HttpSecurity> verificationFiltersConfigurer;
    private String smsCodeLoginVerificationTokenParameter;
    private SmsCodeService smsCodeLoginSmsCodeVerificationTokenService;
    private String formLoginVerificationTokenParameter;
    private TokenService<?> formLoginRequestVerificationTokenService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        AuthenticationSuccessHandler delegateFormLoginSuccessHandler
                = new FormLoginSuccessHandler(formLoginAttemptsLimiter, formLoginSuccessHandler);

        AuthenticationFailureHandler delegateFormLoginFailureHandler
                = new FormLoginFailureHandler(formLoginAttemptsLimiter, formLoginAttemptsLimitHandler, formLoginFailureHandler);

        http.formLogin()
                .loginProcessingUrl(formLoginProcessingUrl)
                .successHandler(delegateFormLoginSuccessHandler)
                .failureHandler(delegateFormLoginFailureHandler);

        enableFormLoginRequestVerification(http);

        getOrApplyFormLoginAttemptsLimitConfigurer(http)
            .formLoginProcessingRequestMatcher(new AntPathRequestMatcher(formLoginProcessingUrl, "POST"))
            .attemptsLimiter(formLoginAttemptsLimiter)
            .attemptsLimitHandler(formLoginAttemptsLimitHandler);




        getOrApplySmsCodeLoginConfigurer(http)
            .mobilePhoneAttribute(WebAttributes.SMS_CODE_LOGIN_USERNAME_ATTRIBUTE)
            .loginProcessingUrl(smsCodeLoginProcessingUrl)
            .successHandler(smsCodeLoginSuccessHandler)
            .failureHandler(smsCodeLoginFailureHandler);

        enableSmsCodeLoginSmsCodeVerification(http);




        http.authorizeRequests()
                .antMatchers(
                    formLoginProcessingUrl,
                    formLoginDefaultTargetUrl,
                    formLoginFailureUrl,
                    smsCodeLoginProcessingUrl,
                    smsCodeLoginDefaultTargetUrl,
                    smsCodeLoginFailureUrl,
                    verificationServiceAcquiresTokenUrl
                ).permitAll();
        // @formatter:on
    }

    private void enableFormLoginRequestVerification(HttpSecurity http) throws Exception {

        RequestMatcher requestMatcher   = new RequiresVerificationFormLoginRequestMatcher(formLoginProcessingUrl);
        VerificationFailureHandler formLoginVerificationFailureHandler
                = (request, response, exception) ->
                formLoginFailureHandler.onAuthenticationFailure(
                        request, response,
                        new AuthenticationException(exception.getMessage()){}
                );

        VerificationProvider<?> formLoginImageCodeVerifier = new VerificationProvider<>(
                requestMatcher, formLoginVerificationTokenParameter,
                formLoginRequestVerificationTokenService,
                formLoginVerificationFailureHandler
        );

        getOrApplyVerificationConfigurer(http)
                .processing().addProvider(formLoginImageCodeVerifier);
    }

    protected FormLoginAttemptsLimitConfigurer getOrApplyFormLoginAttemptsLimitConfigurer(HttpSecurity http) throws Exception {
        @SuppressWarnings("unchecked")
        FormLoginAttemptsLimitConfigurer<HttpSecurity> configurer = http.getConfigurer(FormLoginAttemptsLimitConfigurer.class);
        if (configurer == null) {
            configurer = http.apply(new FormLoginAttemptsLimitConfigurer<>());
        }
        return configurer;
    }


    protected SmsCodeLoginConfigurer<HttpSecurity> getOrApplySmsCodeLoginConfigurer(HttpSecurity http) throws Exception {
        @SuppressWarnings("unchecked")
        SmsCodeLoginConfigurer<HttpSecurity> configurer = http.getConfigurer(SmsCodeLoginConfigurer.class);
        if (configurer == null) {
            configurer = http.apply(new SmsCodeLoginConfigurer<>());
        }
        return configurer;
    }

    private void enableSmsCodeLoginSmsCodeVerification(HttpSecurity http) throws Exception {

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
                smsCodeLoginProcessingRequestMatcher, smsCodeLoginVerificationTokenParameter,
                smsCodeLoginSmsCodeVerificationTokenService,
                smsCodeLoginSmsCodeVerificationSuccessHandler,
                smsCodeLoginSmsCodeVerificationFailureHandler
        );

        getOrApplyVerificationConfigurer(http)
                .processing().addProvider(smsCodeLoginSmsCodeVerifier);
    }

    protected VerificationFiltersConfigurer<HttpSecurity> getOrApplyVerificationConfigurer(HttpSecurity http) throws Exception {
        @SuppressWarnings("unchecked")
        VerificationFiltersConfigurer<HttpSecurity> configurer = http.getConfigurer(VerificationFiltersConfigurer.class);
        if(configurer == null) {
            configurer = http.apply(verificationFiltersConfigurer);
        }
        return configurer;
    }

}
