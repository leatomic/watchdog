package io.watchdog.security.web.verification;

import java.util.Map;

public interface TokenService<T extends VerificationToken> {

    T allocate(Map<String, String[]> params);

    T verify(String key);

}
