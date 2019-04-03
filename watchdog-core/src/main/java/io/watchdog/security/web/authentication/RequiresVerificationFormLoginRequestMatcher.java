package io.watchdog.security.web.authentication;

import io.watchdog.security.web.WebAttributes;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

public class RequiresVerificationFormLoginRequestMatcher implements RequestMatcher {

    private final RequestMatcher formLoginProcessingRequestMatcher;

    public RequiresVerificationFormLoginRequestMatcher(String formLoginProcessingUrl) {
        this.formLoginProcessingRequestMatcher = new AntPathRequestMatcher(formLoginProcessingUrl, "POST");
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return formLoginProcessingRequestMatcher.matches(request) && requiresVerification(request);
    }

    public static boolean requiresVerification(HttpServletRequest request) {
        return WebUtils.getSessionAttribute(request, WebAttributes.FORM_LOGIN_REQUIRES_VERIFICATION_TOKEN) != null;
    }

}
