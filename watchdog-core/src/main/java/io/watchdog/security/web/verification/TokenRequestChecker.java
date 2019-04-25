package io.watchdog.security.web.verification;

public interface TokenRequestChecker<T extends TokenRequest> {

    void check(T request);

}
