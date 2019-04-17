package io.watchdog.security.config.annotation.web;

import io.watchdog.autoconfigure.properties.AuthenticationProperties;
import io.watchdog.security.config.annotation.web.configurers.VerificationFiltersConfigurer;
import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import io.watchdog.security.verification.TokenService;
import io.watchdog.security.web.verification.sms.SmsCodeService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Getter @Setter
public class BrowserWebSecurityConfigurer extends CoreWebSecurityConfigurer {

    public BrowserWebSecurityConfigurer(
            AuthenticationProperties authenticationProperties,
            AuthenticationSuccessHandler formLoginSuccessHandler, AuthenticationFailureHandler formLoginFailureHandler,
            TokenService<?> formLoginRequestVerificationTokenService,
            FormLoginAttemptsLimiter formLoginAttemptsLimiter,
            AuthenticationSuccessHandler smsCodeSuccessHandler, AuthenticationFailureHandler smsCodeLoginFailureHandler,
            SmsCodeService smsCodeLoginSmsCodeVerificationTokenService,
            VerificationFiltersConfigurer<HttpSecurity> verificationFiltersConfigurer) {
        super(authenticationProperties,
                formLoginSuccessHandler, formLoginFailureHandler, formLoginRequestVerificationTokenService, formLoginAttemptsLimiter,
                smsCodeSuccessHandler, smsCodeLoginFailureHandler, smsCodeLoginSmsCodeVerificationTokenService,
                verificationFiltersConfigurer
        );
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        String loginPageUrl = getAuthenticationProperties().getLoginPageUrl();
        http.formLogin().loginPage(loginPageUrl);

    }
}
