package io.watchdog.autoconfigure;

import io.watchdog.autoconfigure.properties.AuthenticationProperties;
import io.watchdog.autoconfigure.properties.VerificationProperties;
import io.watchdog.security.config.annotation.web.configurers.VerificationFiltersConfigurer;
import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import io.watchdog.security.web.authentication.RedisFormLoginAttemptsLimiter;
import io.watchdog.security.web.verification.*;
import io.watchdog.security.web.verification.impl.image.DefaultImageCodeWriter;
import io.watchdog.security.web.verification.impl.image.ImageCode;
import io.watchdog.security.web.verification.impl.image.ImageCodeService;
import io.watchdog.security.web.verification.impl.sms.SmsCode;
import io.watchdog.security.web.verification.impl.sms.SmsCodeConsoleWriter;
import io.watchdog.security.web.verification.impl.sms.SmsCodeService;
import io.watchdog.validation.MobilePhoneValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Configuration
@Import({ValidationConfiguration.class, CoreWebSecurityAutoConfiguration.VerificationConfiguration.class})
@EnableConfigurationProperties({AuthenticationProperties.class})
@PropertySource("classpath:/config/authentication.yml")
@EnableWebSecurity
public class CoreWebSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        log.info("Using default Password Encoder: BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    // ~ form login
    // =================================================================================================================
    @Bean
    @ConditionalOnMissingBean(name = "formLoginAttemptsLimiter")
    public FormLoginAttemptsLimiter formLoginAttemptsLimiter(RedisTemplate<String, Long> longRedisTemplate, AuthenticationProperties authenticationProperties) {

        AuthenticationProperties.FormLogin.AttemptsLimit attemptsLimitProperties = authenticationProperties.getFormLogin().getAttemptsLimit();
        return new RedisFormLoginAttemptsLimiter(longRedisTemplate, attemptsLimitProperties.getWarningFailureAttempts(), attemptsLimitProperties.getMaximumFailureAttempts());
    }

    @Bean
    @ConditionalOnMissingBean(name = "formLoginRequestVerificationTokenService")
    public TokenService formLoginRequestVerificationTokenService(ImageCodeService imageCodeService) {
        return imageCodeService;
    }

    // ~ sms code login
    // =================================================================================================================

    @Bean
    @ConditionalOnMissingBean(name = "smsCodeLoginSmsCodeVerificationTokenService")
    public SmsCodeService smsCodeLoginSmsCodeVerificationTokenService(SmsCodeService smsCodeService) {
        return smsCodeService;
    }

    // ! verification config
    // =================================================================================================================
    @Configuration
    @EnableConfigurationProperties({VerificationProperties.class})
    public class VerificationConfiguration {

        @Bean
        public ImageCodeService imageCodeService(VerificationProperties verificationProperties, TokenRepository<ImageCode> imageCodeRepository) {
            VerificationProperties.Service.ImageCodeService
                    imageCodeServiceProperties = verificationProperties.getService().getImageCode();

            int seqLength           = imageCodeServiceProperties.getSeqLength();
            int expireIn            = imageCodeServiceProperties.getExpireIn();
            int defaultImageWidth   = imageCodeServiceProperties.getDefaultImageWidth();
            int defaultImageHeight  = imageCodeServiceProperties.getDefaultImageHeight();

            return new ImageCodeService(
                    seqLength, Duration.of(expireIn, ChronoUnit.SECONDS),
                    imageCodeRepository,
                    defaultImageWidth, defaultImageHeight
            );
        }

        @Bean
        @ConditionalOnMissingBean(name = "imageCodeWriter")
        public TokenWriter<ImageCode> imageCodeWriter() {
            return new DefaultImageCodeWriter();
        }

        @Bean
        public VerificationService<ImageCode> imageCodeVerificationService(
                VerificationProperties verificationProperties,
                ImageCodeService imageCodeService,
                TokenWriter<ImageCode> imageCodeWriter
        ) {
            String supportsTokenType = verificationProperties.getService().getImageCode().getSupportsTokenType();
            return new VerificationService<>(supportsTokenType, imageCodeService, imageCodeWriter);
        }




        @Bean
        public SmsCodeService smsCodeService(VerificationProperties verificationProperties, TokenRepository<SmsCode> smsCodeRepository, MobilePhoneValidator mobilePhoneValidator) {
            VerificationProperties.Service.SmsCodeService
                    smsCodeServiceProperties = verificationProperties.getService().getSmsCode();
            int seqLength   = smsCodeServiceProperties.getSeqLength();
            int expireIn    = smsCodeServiceProperties.getExpireIn();
            String toMobileParameter = smsCodeServiceProperties.getToMobileParameter();
            return new SmsCodeService(
                    seqLength, Duration.of(expireIn, ChronoUnit.SECONDS),
                    toMobileParameter,
                    smsCodeRepository,
                    mobilePhoneValidator
            );
        }

        @Bean
        @ConditionalOnMissingBean(name = "smsCodeWriter")
        public TokenWriter<SmsCode> smsCodeWriter() {
            return new SmsCodeConsoleWriter();
        }

        @Bean
        public VerificationService<SmsCode> smsCodeVerificationService(VerificationProperties verificationProperties, SmsCodeService smsCodeService, TokenWriter<SmsCode> smsCodeWriter) {
            String supportsTokenType = verificationProperties.getService().getSmsCode().getSupportsTokenType();
            return new VerificationService<>(supportsTokenType, smsCodeService, smsCodeWriter);
        }

        @Bean
        public VerificationFiltersConfigurer<HttpSecurity> verificationFiltersConfigurer(
                VerificationProperties verificationProperties,
                List<VerificationService> verificationServices,
                VerificationServiceFailureHandler verificationServiceFailureHandler,
                @Autowired(required = false) List<VerificationProvider> verificationProviders
        ) {

            VerificationFiltersConfigurer<HttpSecurity> configurer = new VerificationFiltersConfigurer<>();

            VerificationProperties.Service tokenServiceProperties = verificationProperties.getService();
            // @formatter:off
            configurer.tokenEndpoint()
                            .acquiresTokenUrl(tokenServiceProperties.getAcquiresTokenUrl())
                            .tokenTypeParameter(tokenServiceProperties.getTokenTypeParameter())
                            .addServices(verificationServices)
                            .serviceFailureHandler(verificationServiceFailureHandler)
                .and().processing()
                            .addProviders(verificationProviders);
            // @formatter:on
            return configurer;
        }

    }

}

