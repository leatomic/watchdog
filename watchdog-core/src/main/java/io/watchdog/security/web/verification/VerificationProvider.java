package io.watchdog.security.web.verification;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Getter @Setter
public class VerificationProvider<T extends VerificationToken> {

    private static final String DEFAULT_TOKEN_PARAMETER = "verification-token";

    private RequestMatcher requestMatcher;
    private String tokenParameter;
    private TokenService<T> service;
    private VerificationSuccessHandler<T> successHandler;
    private VerificationFailureHandler failureHandler;

    @SuppressWarnings("unchecked")
    public VerificationProvider(RequestMatcher requestMatcher, String tokenParameter,
                                TokenService<T> service,
                                VerificationFailureHandler failureHandler) {
        this(requestMatcher, tokenParameter, service, new NonOpVerificationSuccessHandler(), failureHandler);
    }


    public VerificationProvider(RequestMatcher requestMatcher, String tokenParameter,
                                TokenService<T> service,
                                VerificationSuccessHandler<T> successHandler, VerificationFailureHandler failureHandler) {

        this.requestMatcher = Objects.requireNonNull(requestMatcher);

        if (StringUtils.isBlank(tokenParameter)) throw new IllegalArgumentException("tokenParameter can not be null nor empty");
        this.tokenParameter = tokenParameter;

        this.service = Objects.requireNonNull(service);

        this.successHandler = Objects.requireNonNull(successHandler);
        this.failureHandler = Objects.requireNonNull(failureHandler);
    }


    public boolean tryVerify(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (!requestMatcher.matches(request)) {
            return true;
        }

        T verified;
        try {
            String presentedKey = obtainTokenKey(request);
            verified = service.verify(presentedKey);
        }
        catch (InternalVerificationException ive) {
            log.error(ive.getMessage());
            failureHandler.onVerificationFailure(request, response, ive);
            return false;
        }
        catch (VerificationException ve) {
            failureHandler.onVerificationFailure(request, response, ve);
            return false;
        }

        if (verified == null)
            throw new IllegalStateException("token service must return a non-null token instance after calling verify()");

        successHandler.onVerificationSuccess(request, verified);
        return true;
    }

    private String obtainTokenKey(HttpServletRequest request) {
        String tokenKey = request.getParameter(tokenParameter);
        if (tokenKey == null || tokenKey.isEmpty()) {
            throw new VerificationException("验证码未提交或为空");
        }

        return tokenKey;
    }

    private static class NonOpVerificationSuccessHandler implements VerificationSuccessHandler {

        @Override
        public void onVerificationSuccess(HttpServletRequest request, VerificationToken token) {
            if (log.isDebugEnabled()) {
                log.debug("request '" + request.getPathInfo() + "' has been verified, token: " + token.toString());
            }
        }
    }

}
