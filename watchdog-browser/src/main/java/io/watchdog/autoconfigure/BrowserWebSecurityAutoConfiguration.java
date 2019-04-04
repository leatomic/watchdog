package io.watchdog.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.watchdog.autoconfigure.properties.AuthenticationProperties;
import io.watchdog.autoconfigure.properties.VerificationProperties;
import io.watchdog.http.SimpleResponseBody;
import io.watchdog.security.config.annotation.web.BrowserWebSecurityConfigurer;
import io.watchdog.security.config.annotation.web.configurers.VerificationFiltersConfigurer;
import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import io.watchdog.security.web.authentication.FormLoginFailureHandler;
import io.watchdog.security.web.authentication.FormLoginSuccessHandler;
import io.watchdog.security.web.verification.*;
import io.watchdog.security.web.verification.impl.image.ImageCode;
import io.watchdog.security.web.verification.impl.sms.SmsCode;
import io.watchdog.security.web.verification.impl.sms.SmsCodeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@Import(BrowserWebSecurityAutoConfiguration.VerificationConfiguration.class)
@ComponentScan(basePackages = {"io.watchdog.security.authentication.provider.endpoint"})
public class BrowserWebSecurityAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(name = "formLoginSuccessHandler")
    protected AuthenticationSuccessHandler formLoginSuccessHandler(AuthenticationProperties authenticationProperties, FormLoginAttemptsLimiter formLoginAttemptsLimiter) {
        String defaultTargetUrl = authenticationProperties.getFormLogin().getDefaultTargetUrl();
        return new FormLoginSuccessHandler(defaultTargetUrl, formLoginAttemptsLimiter);
    }

    @Bean
    @ConditionalOnMissingBean(name = "formLoginFailureHandler")
    protected AuthenticationFailureHandler formLoginFailureHandler(AuthenticationProperties authenticationProperties, FormLoginAttemptsLimiter formLoginAttemptsLimiter) {
        String failureUrl = authenticationProperties.getFormLogin().getFailureUrl();
        return new FormLoginFailureHandler(failureUrl, formLoginAttemptsLimiter);
    }


    @Bean
    @ConditionalOnMissingBean(name = "smsCodeLoginSuccessHandler")
    protected AuthenticationSuccessHandler smsCodeLoginSuccessHandler(AuthenticationProperties authenticationProperties) {
        String defaultTargetUrl = authenticationProperties.getFormLogin().getDefaultTargetUrl();
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setDefaultTargetUrl(defaultTargetUrl);
        return successHandler;
    }

    @Bean
    @ConditionalOnMissingBean(name = "smsCodeLoginFailureHandler")
    protected AuthenticationFailureHandler smsCodeLoginFailureHandler(AuthenticationProperties authenticationProperties) {
        String failureUrl = authenticationProperties.getFormLogin().getFailureUrl();
        return new SimpleUrlAuthenticationFailureHandler(failureUrl);
    }


    @Bean
    public WebSecurityConfigurer<WebSecurity> baseWebSecurityConfigurer(
            AuthenticationProperties authenticationProperties,
            AuthenticationFailureHandler formLoginFailureHandler, AuthenticationSuccessHandler formLoginSuccessHandler,
            TokenService<?> formLoginRequestVerificationTokenService,
            FormLoginAttemptsLimiter formLoginAttemptsLimiter,
            AuthenticationFailureHandler smsCodeLoginFailureHandler, AuthenticationSuccessHandler smsCodeLoginSuccessHandler,
            SmsCodeService smsCodeLoginSmsCodeVerificationTokenService,
            VerificationFiltersConfigurer<HttpSecurity> verificationFiltersConfigurer
    ) {
        return new BrowserWebSecurityConfigurer(
                authenticationProperties,
                formLoginSuccessHandler, formLoginFailureHandler, formLoginRequestVerificationTokenService, formLoginAttemptsLimiter,
                smsCodeLoginSuccessHandler, smsCodeLoginFailureHandler, smsCodeLoginSmsCodeVerificationTokenService,
                verificationFiltersConfigurer
        );
    }


    @Configuration
    public static class VerificationConfiguration {

        @Bean
        @ConditionalOnMissingBean(VerificationServiceFailureHandler.class)
        public VerificationServiceFailureHandler verificationServiceFailureHandler(ObjectMapper objectMapper) {
            return (request, response, exception) -> {
                HttpStatus status = exception instanceof InternalTokenServiceException
                        ? HttpStatus.INTERNAL_SERVER_ERROR
                        : HttpStatus.BAD_REQUEST;
                response.setStatus(status.value());
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.getWriter().write(
                        objectMapper.writeValueAsString(
                                new SimpleResponseBody(exception.getClass().getName(), exception.getLocalizedMessage(), null)
                        )
                );
            };
        }

        @Bean
        @ConditionalOnMissingBean(VerificationFailureHandler.class)
        public VerificationFailureHandler verificationFailureHandler(ObjectMapper objectMapper) {
            return (request, response, exception) -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.getWriter().write(
                        objectMapper.writeValueAsString(
                                new SimpleResponseBody(exception.getClass().getName(), exception.getLocalizedMessage(), null)
                        )
                );
            };
        }

        @Bean
        public TokenRepository<ImageCode> imageCodeRepository(VerificationProperties verificationProperties) {
            String tokenAttribute   = verificationProperties.getService().getImageCode().getRepository().getTokenAttribute();
            return new HttpSessionTokenRepository<ImageCode>(tokenAttribute) {};
        }

        @Bean
        public TokenRepository<SmsCode> smsCodeRepository(VerificationProperties verificationProperties) {
            String tokenAttribute   = verificationProperties.getService().getSmsCode().getRepository().getTokenAttribute();
            return new HttpSessionTokenRepository<SmsCode>(tokenAttribute) {};
        }

    }

}
