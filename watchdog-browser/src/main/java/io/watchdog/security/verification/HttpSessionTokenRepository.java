package io.watchdog.security.verification;

import io.watchdog.security.web.verification.TokenRepository;
import io.watchdog.security.web.verification.VerificationRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

public class HttpSessionTokenRepository<T extends VerificationToken> implements TokenRepository<T> {

    private static final String PREFIX = "verification-token:";

    @Override @SuppressWarnings("unchecked")
    public T load(VerificationRequest.Type forTokenRequestType) {
        return (T) currentSession().getAttribute(assembleAttributeName(forTokenRequestType));
    }

    @Override
    public void save(VerificationRequest.Type forTokenRequestType, T token) {
        currentSession().setAttribute(assembleAttributeName(forTokenRequestType), token);
    }

    @Override
    public void remove(VerificationRequest.Type forTokenRequestType, T token) {
        currentSession().removeAttribute(assembleAttributeName(forTokenRequestType));
    }

    private HttpSession currentSession() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return servletRequestAttributes.getRequest().getSession(true);
    }

    private String assembleAttributeName(VerificationRequest.Type forTokenRequestType) {
        return PREFIX + forTokenRequestType.getTokenType() + ":" + forTokenRequestType.getBusiness();
    }

}
