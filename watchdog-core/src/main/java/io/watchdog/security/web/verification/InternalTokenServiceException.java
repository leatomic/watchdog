package io.watchdog.security.web.verification;

public class InternalTokenServiceException extends TokenServiceException {

    public InternalTokenServiceException(String message) {
        super(message);
    }

    public InternalTokenServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
