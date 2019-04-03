package io.watchdog.security.web.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
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

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private RequestMatcher formLoginProcessingRequestMatcher;
    private FormLoginAttemptsLimiter limiter;

    private String attemptsLimitedForwardUrl;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (formLoginProcessingRequestMatcher.matches(request)) {
            boolean canDoAttempt = limiter.canReach(new FormLoginDetails(request));
            if (!canDoAttempt) {
                onAttemptsLimited(request, response);
            }
        }

        chain.doFilter(req, res);

    }

    private void onAttemptsLimited(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.sendRedirect(request, response, attemptsLimitedForwardUrl);
    }



}
