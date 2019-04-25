package io.watchdog.security.web.authentication;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectFormLoginDisabledHandler implements FormLoginDisabledHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private String loginDisabledUrl;

    public RedirectFormLoginDisabledHandler(String loginDisabledUrl) {
        this.loginDisabledUrl = loginDisabledUrl;
    }

    @Override
    public void onFormLoginDisabled(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.sendRedirect(request, response, loginDisabledUrl);
    }
}
