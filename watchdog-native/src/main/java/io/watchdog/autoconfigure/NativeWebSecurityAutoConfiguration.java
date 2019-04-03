package io.watchdog.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.watchdog.autoconfigure.properties.VerificationProperties;
import io.watchdog.security.web.verification.RestVerificationFailureHandler;
import io.watchdog.security.web.verification.RestVerificationServiceFailureHandler;
import io.watchdog.security.web.verification.VerificationFailureHandler;
import io.watchdog.security.web.verification.VerificationServiceFailureHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Slf4j
@Configuration
@Import({NativeWebSecurityAutoConfiguration.VerificationConfiguration.class})
@EnableWebSecurity
public class NativeWebSecurityAutoConfiguration {

    @Configuration
    @EnableConfigurationProperties({VerificationProperties.class})
    public class VerificationConfiguration {

        @Bean
        @ConditionalOnMissingBean(VerificationServiceFailureHandler.class)
        public VerificationServiceFailureHandler verificationServiceFailureHandler(ObjectMapper objectMapper) {
            return new RestVerificationServiceFailureHandler(objectMapper);
        }

        @Bean
        @ConditionalOnMissingBean(VerificationFailureHandler.class)
        public VerificationFailureHandler verificationFailureHandler(ObjectMapper objectMapper) {
            return new RestVerificationFailureHandler(objectMapper);
        }

    }


}
