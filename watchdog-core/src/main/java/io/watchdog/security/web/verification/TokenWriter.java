package io.watchdog.security.web.verification;

import io.watchdog.security.verification.VerificationToken;

import java.io.IOException;

public interface TokenWriter<T extends VerificationToken> {

    void write(T token) throws IOException;

}
