package io.watchdog.security.web.verification;


import io.watchdog.security.verification.VerificationToken;

public interface TokenRepository<T extends VerificationToken> {

    T load(VerificationRequest.Type forTokenRequestType);

    void save(VerificationRequest.Type forTokenRequestType, T token);

    // TODO 替换成如果与期望值相等就删除
    void remove(VerificationRequest.Type forTokenRequestType, T token);

}