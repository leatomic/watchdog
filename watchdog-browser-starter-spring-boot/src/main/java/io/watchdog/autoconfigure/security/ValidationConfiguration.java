package io.watchdog.autoconfigure.security;

import io.watchdog.validation.MobilePhoneValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfiguration {

    @Bean
    @ConditionalOnMissingBean(MobilePhoneValidator.class)
    protected MobilePhoneValidator mobileValidator() {
        return new MobilePhoneValidator();
    }

}
