package io.watchdog.autoconfigure;

import io.watchdog.validation.MobilePhoneValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfiguration {

    @Bean
    @ConditionalOnMissingBean(EmailValidator.class)
    protected EmailValidator emailValidator() {
        return new EmailValidator();
    }

    @Bean
    @ConditionalOnMissingBean(MobilePhoneValidator.class)
    protected MobilePhoneValidator mobileValidator() {
        return new MobilePhoneValidator();
    }

}
