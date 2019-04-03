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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Configuration
@Import({ValidationConfiguration.class, CoreWebSecurityAutoConfiguration.VerificationConfiguration.class})
@EnableConfigurationProperties({AuthenticationProperties.class})
@EnableWebSecurity
public class CoreWebSecurityAutoConfiguration {

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @ConditionalOnMissingBean(AuthenticationManager.class)
    public AuthenticationManager authenticationManagerBean(WebSecurityConfigurer<WebSecurity> webSecurityConfigurer) throws Exception {
        return ((WebSecurityConfigurerAdapter) webSecurityConfigurer).authenticationManagerBean();
    }

    // ~ form login
    // =================================================================================================================
    @Bean
    @ConditionalOnMissingBean(name = "formLoginAttemptsLimiter")
    public FormLoginAttemptsLimiter formLoginAttemptsLimiter(RedisTemplate<String, Long> longRedisTemplate) {
        return new RedisFormLoginAttemptsLimiter(longRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = "formLoginRequestVerificationTokenService")
    public TokenService formLoginRequestVerificationTokenService(ImageCodeService imageCodeService) {
        return imageCodeService;
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
        public VerificationService<ImageCode> imageCodeVerificationService(VerificationProperties verificationProperties, ImageCodeService imageCodeService) {
            String supportsTokenType = verificationProperties.getService().getImageCode().getSupportsTokenType();
            return new VerificationService<>(supportsTokenType, imageCodeService, imageCodeWriter());
        }



        @Bean
        public VerificationFiltersConfigurer<HttpSecurity> verificationFiltersConfigurer(
                VerificationProperties verificationProperties,
                List<VerificationService> verificationServices,
                VerificationServiceFailureHandler verificationServiceFailureHandler,
                @Autowired(required = false) List<VerificationProvider> verificationProviders,
                VerificationFailureHandler verificationFailureHandler
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
                            .addProviders(verificationProviders)
                            .failureHandler(verificationFailureHandler);
            // @formatter:on
            return configurer;
        }

    }

}

