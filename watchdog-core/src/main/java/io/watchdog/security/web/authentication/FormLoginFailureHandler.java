package io.watchdog.security.web.authentication;

import io.watchdog.security.web.WebAttributes;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FormLoginFailureHandler implements AuthenticationFailureHandler {

    private AuthenticationFailureHandler target;

    private FormLoginAttemptsLimiter attemptsLimiter;
    private FormLoginAttemptsLimitHandler attemptsLimitedHandler;

    public FormLoginFailureHandler(
            FormLoginAttemptsLimiter attemptsLimiter, FormLoginAttemptsLimitHandler attemptsLimitedHandler,
            AuthenticationFailureHandler target) {
        this.attemptsLimiter = attemptsLimiter;
        this.attemptsLimitedHandler = attemptsLimitedHandler;
        this.target = target;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof BadCredentialsException) {

            FormLoginAttemptsLimiter.Feedback feedback = attemptsLimiter.recordFailure(new FormLoginDetails(request));


            if (feedback.warning()) {
                // 下次表单登录的请求将强制提交验证码
                WebUtils.setSessionAttribute(request, WebAttributes.FORM_LOGIN_REQUIRES_VERIFICATION_TOKEN, true);
            }

            if (feedback.neverAgain()) {
                attemptsLimitedHandler.onAttemptsLimited(request, response);
                return;
            }

        }

        target.onAuthenticationFailure(request, response, exception);
    }
}