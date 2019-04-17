package io.watchdog.security.verification;

import io.watchdog.security.web.verification.VerificationException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Getter @Setter
public abstract class GeneralTokenService<T extends VerificationToken> implements TokenService<T> {

    private TokenRepository<T> tokenRepository;
    private int codeLength;
    private Duration codeValidityDuration;


    public GeneralTokenService(int codeLength,
                               Duration codeValidityDuration,
                               TokenRepository<T> tokenRepository) {
        this.codeLength = codeLength;
        this.codeValidityDuration = codeValidityDuration;
        this.tokenRepository = tokenRepository;
    }



    public T allocate(Map<String, String[]> params) {
        T token = createToken(params);
        tokenRepository.save(token);
        return token;
    }

    protected abstract T createToken(Map<String, String[]> params);




    public T verify(String presentedKey) {
        if (StringUtils.isBlank(presentedKey)) {
            throw new VerificationException("请求中提交的验证码的值不能为空");
        }

        T tokenSaved = tokenRepository.load();
        if (tokenSaved == null || tokenSaved.isExpired()) {
            throw new VerificationException("验证码已过期或已被清除，请重新获取并提交验证码");
        }

        String originalKey = tokenSaved.getKey();
        doMatches(presentedKey, originalKey);

        tokenRepository.clear();

        return tokenSaved;
    }

    protected void doMatches(String presentedKey, String originalKey) {
        if (!originalKey.trim().equalsIgnoreCase(presentedKey.trim())) {
            throw new VerificationException("验证码输入错误");
        }
    }

}
