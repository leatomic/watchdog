package io.watchdog.security.verification;


public interface TokenRepository<T extends VerificationToken> {

    T load();

    void save(T token);

    void clear();

}