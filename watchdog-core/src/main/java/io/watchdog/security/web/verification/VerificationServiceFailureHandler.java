package io.watchdog.security.web.verification;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface VerificationServiceFailureHandler {

    void onVerificationServiceFailure(HttpServletRequest request, HttpServletResponse response,
                                      TokenServiceException exception
                                            ) throws IOException, ServletException;

}
