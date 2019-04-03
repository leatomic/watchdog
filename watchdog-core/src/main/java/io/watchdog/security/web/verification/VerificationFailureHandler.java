package io.watchdog.security.web.verification;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface VerificationFailureHandler {

    void onVerificationFailure( HttpServletRequest request, HttpServletResponse response,
                                VerificationException exception) throws IOException, ServletException;
}
