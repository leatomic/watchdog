package io.watchdog.security.web.verification;

import java.io.IOException;

public interface TokenWriter<T extends VerificationToken> {

    void write(T token) throws IOException;

}
