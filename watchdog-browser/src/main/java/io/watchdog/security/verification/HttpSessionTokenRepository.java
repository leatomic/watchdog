package io.watchdog.security.verification;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

public class HttpSessionTokenRepository<T extends VerificationToken> implements TokenRepository<T> {

    private String tokenAttributeName;

    public HttpSessionTokenRepository(String tokenAttributeName) {
        this.tokenAttributeName = tokenAttributeName;
    }

    private HttpSession currentSession() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return servletRequestAttributes.getRequest().getSession(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T load() {
        return (T) currentSession().getAttribute(tokenAttributeName);
    }

    @Override
    public void save(T token) {
        currentSession().setAttribute(tokenAttributeName, token);
    }

    @Override
    public void clear() {
        currentSession().removeAttribute(tokenAttributeName);
    }
}
