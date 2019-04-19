package io.watchdog.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.watchdog.autoconfigure.properties.AuthenticationProperties;
import io.watchdog.autoconfigure.properties.VerificationProperties;
import io.watchdog.http.SimpleResponseBody;
import io.watchdog.security.config.annotation.web.BrowserWebSecurityConfigurer;
import io.watchdog.security.config.annotation.web.configurers.VerificationFiltersConfigurer;
import io.watchdog.security.verification.HttpSessionTokenRepository;
import io.watchdog.security.verification.InternalTokenServiceException;
import io.watchdog.security.verification.TokenRepository;
import io.watchdog.security.verification.TokenService;
import io.watchdog.security.web.authentication.FormLoginAttemptsLimitHandler;
import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import io.watchdog.security.web.authentication.RedirectFormLoginAttemptsLimitHandler;
import io.watchdog.security.web.verification.VerificationFailureHandler;
import io.watchdog.security.web.verification.VerificationServiceFailureHandler;
import io.watchdog.security.web.verification.image.ImageCode;
import io.watchdog.security.web.verification.sms.SmsCode;
import io.watchdog.security.web.verification.sms.SmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
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
@Import({CoreWebSecurityConfiguration.class, BrowserWebSecurityAutoConfiguration.VerificationConfiguration.class})
@ComponentScan(basePackages = {"io.watchdog.security.authentication.provider.endpoint"})
public class BrowserWebSecurityAutoConfiguration {

    @Autowired
    private AuthenticationProperties authenticationProperties;

    @Autowired
    private VerificationProperties verificationProperties;

    @Bean
    public WebSecurityConfigurer<WebSecurity> baseWebSecurityConfigurer(
            AuthenticationSuccessHandler formLoginSuccessHandler,
            AuthenticationFailureHandler formLoginFailureHandler,
            FormLoginAttemptsLimiter formLoginAttemptsLimiter,
            FormLoginAttemptsLimitHandler formLoginAttemptsLimitHandler,

            AuthenticationSuccessHandler smsCodeLoginSuccessHandler,
            AuthenticationFailureHandler smsCodeLoginFailureHandler,

            VerificationFiltersConfigurer<HttpSecurity> verificationFiltersConfigurer,
            SmsCodeService smsCodeLoginSmsCodeVerificationTokenService,
            TokenService<?> formLoginRequestVerificationTokenService
    ) {
        BrowserWebSecurityConfigurer configurer = new BrowserWebSecurityConfigurer();

        configurer.setLoginPageUrl(authenticationProperties.getLoginPageUrl());

        AuthenticationProperties.FormLogin formLogin = authenticationProperties.getFormLogin();
        configurer.setFormLoginProcessingUrl(formLogin.getProcessingUrl());
        configurer.setFormLoginDefaultTargetUrl(formLogin.getDefaultTargetUrl());
        configurer.setFormLoginSuccessHandler(formLoginSuccessHandler);
        configurer.setFormLoginFailureUrl(formLogin.getFailureUrl());
        configurer.setFormLoginFailureHandler(formLoginFailureHandler);
        configurer.setFormLoginAttemptsLimiter(formLoginAttemptsLimiter);
        configurer.setFormLoginAttemptsLimitedUrl(formLogin.getAttemptsLimit().getAttemptLimitedUrl());
        configurer.setFormLoginAttemptsLimitHandler(formLoginAttemptsLimitHandler);

        AuthenticationProperties.SmsCodeLogin smsCodeLogin = authenticationProperties.getSmsCodeLogin();
        configurer.setSmsCodeLoginProcessingUrl(smsCodeLogin.getProcessingUrl());
        configurer.setSmsCodeLoginDefaultTargetUrl(smsCodeLogin.getDefaultTargetUrl());
        configurer.setSmsCodeLoginSuccessHandler(smsCodeLoginSuccessHandler);
        configurer.setSmsCodeLoginFailureUrl(smsCodeLogin.getFailureUrl());
        configurer.setSmsCodeLoginFailureHandler(smsCodeLoginFailureHandler);

        configurer.setVerificationServiceAcquiresTokenUrl(verificationProperties.getService().getAcquiresTokenUrl());
        configurer.setVerificationFiltersConfigurer(verificationFiltersConfigurer);
        configurer.setSmsCodeLoginVerificationTokenParameter(smsCodeLogin.getVerification().getTokenParameter());
        configurer.setSmsCodeLoginSmsCodeVerificationTokenService(smsCodeLoginSmsCodeVerificationTokenService);
        configurer.setFormLoginVerificationTokenParameter(formLogin.getVerification().getTokenParameter());
        configurer.setFormLoginRequestVerificationTokenService(formLoginRequestVerificationTokenService);

        return configurer;
    }

    @Bean
    @ConditionalOnMissingBean(name = "formLoginSuccessHandler")
    protected AuthenticationSuccessHandler formLoginSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setDefaultTargetUrl(authenticationProperties.getFormLogin().getDefaultTargetUrl());
        return successHandler;
    }

    @Bean
    @ConditionalOnMissingBean(name = "formLoginFailureHandler")
    protected AuthenticationFailureHandler formLoginFailureHandler() {
        String failureUrl = authenticationProperties.getFormLogin().getFailureUrl();
        return new SimpleUrlAuthenticationFailureHandler(failureUrl);
    }

    @Bean
    public FormLoginAttemptsLimitHandler formLoginAttemptsLimitHandler() {
        String formLoginAttemptsFailureUrl = authenticationProperties.getFormLogin().getAttemptsLimit().getAttemptLimitedUrl();
        return new RedirectFormLoginAttemptsLimitHandler(formLoginAttemptsFailureUrl);
    }

    @Bean
    @ConditionalOnMissingBean(name = "smsCodeLoginSuccessHandler")
    protected AuthenticationSuccessHandler smsCodeLoginSuccessHandler() {
        String defaultTargetUrl = authenticationProperties.getFormLogin().getDefaultTargetUrl();
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setDefaultTargetUrl(defaultTargetUrl);
        return successHandler;
    }

    @Bean
    @ConditionalOnMissingBean(name = "smsCodeLoginFailureHandler")
    protected AuthenticationFailureHandler smsCodeLoginFailureHandler() {
        String failureUrl = authenticationProperties.getFormLogin().getFailureUrl();
        return new SimpleUrlAuthenticationFailureHandler(failureUrl);
    }



    @Configuration
    public static class VerificationConfiguration {

        @Autowired
        private VerificationProperties verificationProperties;

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
        public TokenRepository<ImageCode> imageCodeRepository() {
            String tokenAttribute   = verificationProperties.getService().getImageCode().getRepository().getTokenAttribute();
            return new HttpSessionTokenRepository<ImageCode>(tokenAttribute) {};
        }

        @Bean
        public TokenRepository<SmsCode> smsCodeRepository() {
            String tokenAttribute   = verificationProperties.getService().getSmsCode().getRepository().getTokenAttribute();
            return new HttpSessionTokenRepository<SmsCode>(tokenAttribute) {};
        }

    }

}
