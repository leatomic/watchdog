package io.watchdog.security.verification;

import io.watchdog.security.web.verification.TokenRequest;

public interface TokenService<R extends TokenRequest, T extends VerificationToken> {

    T allocate(R request);

    void verify(String presentedKey, T tokenSaved);

}
