package io.watchdog.security.verification;

import java.util.Map;

public interface TokenService<T extends VerificationToken> {

    T allocate(Map<String, String[]> params);

    T verify(String key);

}
