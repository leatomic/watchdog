package io.watchdog.security.web.authentication;

import io.watchdog.security.web.WebAttributes;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Getter @Setter
public class FormLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private FormLoginAttemptsLimiter attemptsLimiter;

    public FormLoginFailureHandler(String failureUrl, FormLoginAttemptsLimiter attemptsLimiter) {
        super(failureUrl);
        this.attemptsLimiter = attemptsLimiter;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // 下次表单登录的请求将强制提交验证码
        boolean warningTriggered = !attemptsLimiter.reachAndWithoutWarning(new FormLoginDetails(request));
        if (warningTriggered) {
            WebUtils.setSessionAttribute(request, WebAttributes.FORM_LOGIN_REQUIRES_VERIFICATION_TOKEN, true);
        }

        super.onAuthenticationFailure(request, response, exception);

    }
}
