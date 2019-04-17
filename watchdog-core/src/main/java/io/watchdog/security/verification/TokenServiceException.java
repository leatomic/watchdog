package io.watchdog.security.verification;

import io.watchdog.security.web.verification.VerificationException;

public class TokenServiceException extends VerificationException {

    public TokenServiceException(String message) {
        super(message);
    }

    public TokenServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
