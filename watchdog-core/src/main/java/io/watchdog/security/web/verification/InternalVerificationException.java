package io.watchdog.security.web.verification;

public class InternalVerificationException extends VerificationException {

    public InternalVerificationException(String message) {
        super(message);
    }

    public InternalVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
