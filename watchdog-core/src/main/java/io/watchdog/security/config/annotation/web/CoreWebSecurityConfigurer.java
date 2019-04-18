package io.watchdog.security.config.annotation.web;

import io.watchdog.autoconfigure.properties.AuthenticationProperties;
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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Order(99)
@Getter @Setter
public class CoreWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private AuthenticationProperties authenticationProperties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    /**
     * Creates an instance with the default configuration enabled.
     */
    public CoreWebSecurityConfigurer(
            AuthenticationProperties authenticationProperties,
            AuthenticationSuccessHandler formLoginSuccessHandler, AuthenticationFailureHandler formLoginFailureHandler,
            TokenService<?> formLoginRequestVerificationTokenService,
            FormLoginAttemptsLimiter formLoginAttemptsLimiter, FormLoginAttemptsLimitHandler formLoginAttemptsLimitHandler,
            AuthenticationSuccessHandler smsCodeLoginSuccessHandler, AuthenticationFailureHandler smsCodeLoginFailureHandler,
            SmsCodeService smsCodeLoginSmsCodeVerificationTokenService,
            VerificationFiltersConfigurer<HttpSecurity> verificationFiltersConfigurer) {

        this.authenticationProperties = authenticationProperties;
        this.formLoginSuccessHandler = formLoginSuccessHandler;
        this.formLoginFailureHandler = formLoginFailureHandler;
        this.formLoginRequestVerificationTokenService = formLoginRequestVerificationTokenService;
        this.formLoginAttemptsLimiter = formLoginAttemptsLimiter;
        this.formLoginAttemptsLimitHandler = formLoginAttemptsLimitHandler;

        this.smsCodeLoginSuccessHandler = smsCodeLoginSuccessHandler;
        this.smsCodeLoginFailureHandler = smsCodeLoginFailureHandler;
        this.smsCodeLoginSmsCodeVerificationTokenService = smsCodeLoginSmsCodeVerificationTokenService;

        this.verificationFiltersConfigurer = verificationFiltersConfigurer;
    }

    private AuthenticationSuccessHandler formLoginSuccessHandler;
    private AuthenticationFailureHandler formLoginFailureHandler;
    private TokenService<?> formLoginRequestVerificationTokenService;
    private FormLoginAttemptsLimiter formLoginAttemptsLimiter;
    private FormLoginAttemptsLimitHandler formLoginAttemptsLimitHandler;
    private AuthenticationSuccessHandler smsCodeLoginSuccessHandler;
    private AuthenticationFailureHandler smsCodeLoginFailureHandler;
    private SmsCodeService smsCodeLoginSmsCodeVerificationTokenService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/verification.token").permitAll()
                .antMatchers("/sign-up", "/sign-up/**").permitAll();

        String formLoginProcessingUrl = authenticationProperties.getFormLogin().getProcessingUrl();

        AuthenticationSuccessHandler delegateFormLoginSuccessHandler
                = new FormLoginSuccessHandler(formLoginAttemptsLimiter, this.formLoginSuccessHandler);

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

        String smsCodeLoginProcessingUrl = authenticationProperties.getSmsCodeLogin().getProcessingUrl();
        getOrApplySmsCodeLoginConfigurer(http)
                .mobilePhoneAttribute(WebAttributes.SMS_CODE_LOGIN_USERNAME_ATTRIBUTE)
                .loginProcessingUrl(smsCodeLoginProcessingUrl)
                .successHandler(smsCodeLoginSuccessHandler)
                .failureHandler(smsCodeLoginFailureHandler);


        enableSmsCodeLoginSmsCodeVerification(http);
        http.authorizeRequests()
                .antMatchers(
                        formLoginProcessingUrl,
                        authenticationProperties.getFormLogin().getDefaultTargetUrl(),
                        authenticationProperties.getFormLogin().getFailureUrl(),
                        smsCodeLoginProcessingUrl,
                        authenticationProperties.getSmsCodeLogin().getDefaultTargetUrl(),
                        authenticationProperties.getSmsCodeLogin().getFailureUrl()
                ).permitAll();

    }

    private FormLoginAttemptsLimitConfigurer getOrApplyFormLoginAttemptsLimitConfigurer(HttpSecurity http) throws Exception {
        @SuppressWarnings("unchecked")
        FormLoginAttemptsLimitConfigurer<HttpSecurity> configurer = http.getConfigurer(FormLoginAttemptsLimitConfigurer.class);
        if (configurer == null) {
            configurer = http.apply(new FormLoginAttemptsLimitConfigurer<>());
        }
        return configurer;
    }


    private void enableSmsCodeLoginSmsCodeVerification(HttpSecurity http) throws Exception {
        String smsCodeLoginProcessingUrl = authenticationProperties.getSmsCodeLogin().getProcessingUrl();

        RequestMatcher smsCodeLoginProcessingRequestMatcher   = new AntPathRequestMatcher(smsCodeLoginProcessingUrl, "POST");
        String smsCodeTokenParameter           = authenticationProperties.getSmsCodeLogin().getVerification().getTokenParameter();

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
                smsCodeLoginProcessingRequestMatcher, smsCodeTokenParameter,
                smsCodeLoginSmsCodeVerificationTokenService,
                smsCodeLoginSmsCodeVerificationSuccessHandler,
                smsCodeLoginSmsCodeVerificationFailureHandler
        );

        getOrApplyVerificationConfigurer(http)
                .processing().addProvider(smsCodeLoginSmsCodeVerifier);
    }






    // 激活对表单登录请求的图片验证码验证功能
    protected void enableFormLoginRequestVerification(HttpSecurity http) throws Exception {

        String formLoginProcessingUrl = authenticationProperties.getFormLogin().getProcessingUrl();

        RequestMatcher requestMatcher   = new RequiresVerificationFormLoginRequestMatcher(formLoginProcessingUrl);
        String tokenParameter           = authenticationProperties.getFormLogin().getVerification().getTokenParameter();
        VerificationFailureHandler formLoginVerificationFailureHandler
                                        = (request, response, exception) ->
                                            formLoginFailureHandler.onAuthenticationFailure(
                                                    request, response,
                                                    new AuthenticationException(exception.getMessage()){}
                                            );

        VerificationProvider<?> formLoginImageCodeVerifier = new VerificationProvider<>(
                requestMatcher, tokenParameter,
                formLoginRequestVerificationTokenService,
                formLoginVerificationFailureHandler
        );

        getOrApplyVerificationConfigurer(http)
                .processing().addProvider(formLoginImageCodeVerifier);
    }

    private VerificationFiltersConfigurer<HttpSecurity> verificationFiltersConfigurer;

    protected VerificationFiltersConfigurer<HttpSecurity> getOrApplyVerificationConfigurer(HttpSecurity http) throws Exception {
        @SuppressWarnings("unchecked")
        VerificationFiltersConfigurer<HttpSecurity> configurer = http.getConfigurer(VerificationFiltersConfigurer.class);
        if(configurer == null) {
            configurer = http.apply(verificationFiltersConfigurer);
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

}
