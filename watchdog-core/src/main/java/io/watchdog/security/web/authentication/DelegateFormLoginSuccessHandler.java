package io.watchdog.security.web.authentication;

import io.watchdog.security.web.WebAttributes;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Getter @Setter
public class DelegateFormLoginSuccessHandler implements AuthenticationSuccessHandler {

    private AuthenticationSuccessHandler target;


    private FormLoginAttemptsLimiter attemptsLimiter;

    public DelegateFormLoginSuccessHandler(FormLoginAttemptsLimiter attemptsLimiter, AuthenticationSuccessHandler target) {
        this.attemptsLimiter = attemptsLimiter;
        this.target = target;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        attemptsLimiter.clearNumberOfFailureTimes(new FormLoginDetails(request));

        // enables the next formLogin login request to run without token verification
        WebUtils.setSessionAttribute(request, WebAttributes.FORM_LOGIN_REQUIRES_VERIFICATION_TOKEN, null);

        target.onAuthenticationSuccess(request, response, authentication);
    }
}
