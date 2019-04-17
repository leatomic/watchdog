package io.watchdog.security.web.verification;

import io.watchdog.security.verification.VerificationToken;

import javax.servlet.http.HttpServletRequest;

public interface VerificationSuccessHandler<T extends VerificationToken> {
    void onVerificationSuccess(HttpServletRequest request, T token);
}
