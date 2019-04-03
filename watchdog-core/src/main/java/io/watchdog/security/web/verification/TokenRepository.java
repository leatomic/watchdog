package io.watchdog.security.web.verification;


public interface TokenRepository<T extends VerificationToken> {

    T load();

    void save(T token);

    void clear();

}