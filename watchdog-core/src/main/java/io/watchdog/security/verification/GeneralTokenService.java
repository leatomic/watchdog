package io.watchdog.security.verification;

import io.watchdog.security.web.verification.TokenRequest;
import io.watchdog.security.web.verification.VerificationException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Getter @Setter
public abstract class GeneralTokenService<R extends TokenRequest, T extends VerificationToken>
        implements TokenService<R, T> {

    public void verify(String presentedKey, T tokenSaved) {

        if (StringUtils.isBlank(presentedKey)) {
            throw new VerificationException("请求中提交的验证码的值不能为空");
        }


        if (tokenSaved == null || tokenSaved.isExpired()) {
            throw new VerificationException("验证码已过期或已被清除，请重新获取并提交验证码");
        }

        String originalKey = tokenSaved.getKey();
        doMatches(presentedKey, originalKey);
    }

    protected void doMatches(String presentedKey, String originalKey) {
        if (!originalKey.trim().equalsIgnoreCase(presentedKey.trim())) {
            throw new VerificationException("验证码输入错误");
        }
    }

}
