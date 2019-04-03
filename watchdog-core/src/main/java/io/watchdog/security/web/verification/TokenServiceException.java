package io.watchdog.security.web.verification;

public class TokenServiceException extends VerificationException {

    public TokenServiceException(String message) {
        super(message);
    }

    public TokenServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
