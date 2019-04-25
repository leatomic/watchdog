package io.watchdog.security.web.authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("tests to Class InMemoryFormLoginAttemptsLimiter")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InMemoryFormLoginAttemptsLimiterTest.LocalConfiguration.class)
class InMemoryFormLoginAttemptsLimiterTest {

    @Autowired
    private InMemoryFormLoginAttemptsLimiter attemptsLimiter;


    @DisplayName("Process testing")
    @ParameterizedTest
    @ArgumentsSource(FormLoginDetailsProvider.class)
    void processTesting(FormLoginDetails details) {

        assertEquals(0, attemptsLimiter.getNumberOfFailureTimes(details));

        FormLoginAttemptsLimiter.Feedback feedback;
        int failedAttempts = 0;

        for (; failedAttempts <= attemptsLimiter.getMaximum(); ) {

            feedback = attemptsLimiter.recordFailure(details);
            failedAttempts ++;

            assertEquals(failedAttempts, attemptsLimiter.getNumberOfFailureTimes(details));

            if (failedAttempts < attemptsLimiter.getWarningThreshold()) {
                assertTrue(attemptsLimiter.checkAttempt(details));
                assertFalse(feedback.warning());
                assertFalse(feedback.neverAgain());
            } else if (failedAttempts < attemptsLimiter.getMaximum()) {
                assertTrue(attemptsLimiter.checkAttempt(details));
                assertTrue(feedback.warning());
                assertFalse(feedback.neverAgain());
            } else {
                assertFalse(attemptsLimiter.checkAttempt(details));
                assertTrue(feedback.warning());
                assertTrue(feedback.neverAgain());
            }

        }

        attemptsLimiter.clearNumberOfFailureTimes(details);
        assertTrue(attemptsLimiter.checkAttempt(details));
    }

    @DisplayName("WhenRecordExpired, thenCheckAttemptReturnTrue")
    @RepeatedTest(2)
    void whenRecordExpired_thenCheckAttemptReturnTrue() throws InterruptedException {

        FormLoginDetails details = new FormLoginDetails("127.0.0.1", "admin");

        while (attemptsLimiter.checkAttempt(details)) {
            attemptsLimiter.recordFailure(details);
        }

        // wait for recode to expired
        TimeUnit.NANOSECONDS.sleep(attemptsLimiter.getHowLongWillLoginBeDisabled().toNanos());

        attemptsLimiter.checkAttempt(details);

    }

    public static class FormLoginDetailsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    new FormLoginDetails("127.0.0.1", "admin"),
                    new FormLoginDetails("127.0.0.1", "user")
            ).map(Arguments::of);
        }
    }
    





    @Configuration
    static class LocalConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public InMemoryFormLoginAttemptsLimiter inMemoryFormLoginAttemptsLimiter() {
            return new InMemoryFormLoginAttemptsLimiter(1, 5, Duration.ofSeconds(3));
        }
    }

}