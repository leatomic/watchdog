package io.watchdog.security.web.authentication;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectFormLoginAttemptsLimitHandler implements FormLoginAttemptsLimitHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private String attemptsFailureUrl;

    public RedirectFormLoginAttemptsLimitHandler(String attemptsFailureUrl) {
        this.attemptsFailureUrl = attemptsFailureUrl;
    }

    @Override
    public void onAttemptsLimited(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.sendRedirect(request, response, attemptsFailureUrl);
    }
}
