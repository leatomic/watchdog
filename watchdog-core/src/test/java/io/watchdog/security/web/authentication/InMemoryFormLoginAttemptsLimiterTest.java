package io.watchdog.security.web.authentication;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InMemoryFormLoginAttemptsLimiterTest.LocalConfiguration.class)
class InMemoryFormLoginAttemptsLimiterTest {

    @Autowired
    private InMemoryFormLoginAttemptsLimiter inMemoryFormLoginAttemptsLimiter;


    @Configuration
    static class LocalConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public InMemoryFormLoginAttemptsLimiter inMemoryFormLoginAttemptsLimiter() {
            return new InMemoryFormLoginAttemptsLimiter(1, 5);
        }
    }

}