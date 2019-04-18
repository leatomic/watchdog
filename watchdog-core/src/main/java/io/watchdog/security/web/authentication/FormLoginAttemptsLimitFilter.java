package io.watchdog.security.web.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class FormLoginAttemptsLimitFilter extends GenericFilterBean {

    private RequestMatcher formLoginProcessingRequestMatcher;
    private FormLoginAttemptsLimiter attemptsLimiter;
    private FormLoginAttemptsLimitHandler attemptsLimitedHandler;

    public FormLoginAttemptsLimitFilter(RequestMatcher formLoginProcessingRequestMatcher,
                                        FormLoginAttemptsLimiter attemptsLimiter,
                                        FormLoginAttemptsLimitHandler attemptsLimitedHandler) {
        this.formLoginProcessingRequestMatcher = formLoginProcessingRequestMatcher;
        this.attemptsLimiter = attemptsLimiter;
        this.attemptsLimitedHandler = attemptsLimitedHandler;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (formLoginProcessingRequestMatcher.matches(request)) {
            boolean allowed = attemptsLimiter.checkAttempt(new FormLoginDetails(request));
            if (!allowed) {
                attemptsLimitedHandler.onAttemptsLimited(request, response);
                return;
            }
        }

        chain.doFilter(req, res);

    }





}
