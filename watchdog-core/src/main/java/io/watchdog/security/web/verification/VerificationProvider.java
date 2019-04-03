package io.watchdog.security.web.verification;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@Getter @Setter
public class VerificationProvider<T extends VerificationToken> {

    private static final String DEFAULT_TOKEN_PARAMETER = "verification-token";

    private RequestMatcher requestMatcher;
    private String tokenParameter;
    private TokenService<T> service;
    private VerificationSuccessHandler<T> successHandler;

    @SuppressWarnings("unchecked")
    public VerificationProvider(RequestMatcher requestMatcher, TokenService<T> service) {
        this(requestMatcher, DEFAULT_TOKEN_PARAMETER, service, new DefaultVerificationSuccessHandler());
    }

    @SuppressWarnings("unchecked")
    public VerificationProvider(RequestMatcher requestMatcher, String tokenParameter,
                                TokenService<T> service) {
        this(requestMatcher, tokenParameter, service, new DefaultVerificationSuccessHandler());
    }


    public VerificationProvider(RequestMatcher requestMatcher, String tokenParameter,
                                TokenService<T> service,
                                VerificationSuccessHandler<T> successHandler) {

        this.requestMatcher = Objects.requireNonNull(requestMatcher);

        if (StringUtils.isBlank(tokenParameter)) throw new IllegalArgumentException("tokenParameter can not be null nor empty");
        this.tokenParameter = tokenParameter;

        this.service = Objects.requireNonNull(service);

        this.successHandler = Objects.requireNonNull(successHandler);

    }


    public void verifyIfNecessary(HttpServletRequest request) {

        if (!requestMatcher.matches(request)) {
            return;
        }

        String presentedKey = obtainTokenKey(request);

        T verified = service.verify(presentedKey);

        successHandler.onVerificationSuccess(request, verified);

    }

    private String obtainTokenKey(HttpServletRequest request) {
        String tokenKey = request.getParameter(tokenParameter);
        if (tokenKey == null) {
            throw new VerificationException("parameter '" + tokenParameter + "' not found");
        } else if (tokenKey.isEmpty()) {
            throw new VerificationException("invalid token: '" + tokenKey + "'");
        }
        return tokenKey;
    }

    private static class DefaultVerificationSuccessHandler implements VerificationSuccessHandler {

        @Override
        public void onVerificationSuccess(HttpServletRequest request, VerificationToken token) {
            if (log.isDebugEnabled()) {
                log.debug("request '" + request.getPathInfo() + "' has been verified, token: " + token.toString());
            }
        }
    }

}
