package io.watchdog.security.web.verification;

import io.watchdog.security.verification.TokenService;
import io.watchdog.security.verification.VerificationToken;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Map;

@Getter @Setter
public abstract class VerificationService<R extends TokenRequest, T extends VerificationToken> {

    public static final String DEFAULT_BUSINESS = "universal";

    private final VerificationRequest.Type supportsRequestType;

    // for create token request
    private int codeLength;
    private Duration codeValidityDuration;
    private TokenService<R, T> service;
    private TokenRequestChecker<R> preTokenRequestChecker = new NonopTokenRequestChecker<>();

    private TokenRepository<T> tokenRepository;

    public VerificationService(String supportsTokenType, String forBusiness,
                               int codeLength, Duration codeValidityDuration,
                               TokenService<R, T> service,
                               TokenRepository<T> tokenRepository) {

        this.supportsRequestType = new VerificationRequest.Type(supportsTokenType, forBusiness);

        this.codeLength             = codeLength;
        this.codeValidityDuration   = codeValidityDuration;

        this.service            = service;
        this.tokenRepository    = tokenRepository;
    }


    public boolean supports(VerificationRequest.Type requestType) {
        return supportsRequestType.equals(requestType);
    }


    public T allocate(Map<String, String[]> parameterMap) {
        R request = createRequest(codeLength, codeValidityDuration, parameterMap);
        preTokenRequestChecker.check(request);
        T token = service.allocate(request);
        tokenRepository.save(supportsRequestType, token);
        return token;
    }

    protected abstract R createRequest(int codeLength, Duration codeValidityDuration, Map<String, String[]> parameterMap);


    public T verify(String presentedKey) {
        T saved = tokenRepository.load(supportsRequestType);
        service.verify(presentedKey, saved);
        tokenRepository.remove(supportsRequestType, saved);
        return saved;
    }

    private static class NonopTokenRequestChecker<R extends TokenRequest> implements TokenRequestChecker<R> {

        @Override
        public void check(R request) {
            // do nothing
        }
    }

}
