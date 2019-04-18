package io.watchdog.security.config.annotation.web.configurers;

import io.watchdog.security.web.authentication.FormLoginAttemptsLimitFilter;
import io.watchdog.security.web.authentication.FormLoginAttemptsLimitHandler;
import io.watchdog.security.web.authentication.FormLoginAttemptsLimiter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class FormLoginAttemptsLimitConfigurer <H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<FormLoginAttemptsLimitConfigurer<H>, H> {

    private RequestMatcher requestMatcher;
    private FormLoginAttemptsLimiter attemptsLimiter;
    private FormLoginAttemptsLimitHandler attemptsLimitHandler;

    public FormLoginAttemptsLimitConfigurer<H> formLoginProcessingRequestMatcher(RequestMatcher formLoginProcessingRequestMatcher) {
        this.requestMatcher = formLoginProcessingRequestMatcher;
        return this;
    }

    public FormLoginAttemptsLimitConfigurer<H> attemptsLimiter(FormLoginAttemptsLimiter attemptsLimiter) {
        this.attemptsLimiter = attemptsLimiter;
        return this;
    }

    public FormLoginAttemptsLimitConfigurer<H> attemptsLimitHandler(FormLoginAttemptsLimitHandler attemptsLimitHandler) {
        this.attemptsLimitHandler = attemptsLimitHandler;
        return this;
    }

    @Override
    public void configure(H http) throws Exception {
        FormLoginAttemptsLimitFilter limitFilter = new FormLoginAttemptsLimitFilter(requestMatcher, attemptsLimiter, attemptsLimitHandler);
        http.addFilterBefore(postProcess(limitFilter), UsernamePasswordAuthenticationFilter.class);
    }


}
