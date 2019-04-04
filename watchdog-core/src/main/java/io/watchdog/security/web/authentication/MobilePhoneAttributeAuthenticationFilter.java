package io.watchdog.security.web.authentication;

import io.watchdog.security.authentication.MobilePhoneAuthenticationProvider;
import io.watchdog.security.authentication.MobilePhoneAuthenticationToken;
import io.watchdog.security.web.WebAttributes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证过滤器： 直接在请求中读取指定的属性值，将其作为用户名构建{@link MobilePhoneAuthenticationToken}
 * 并交由{@link MobilePhoneAuthenticationProvider}认证
 */
public class MobilePhoneAttributeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private String mobilePhoneAttribute = WebAttributes.SMS_CODE_LOGIN_USERNAME_ATTRIBUTE;

    // ~ Constructor
    // =================================================================================================================
    public MobilePhoneAttributeAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    // ~
    // =================================================================================================================
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = obtainUsername(request);

        MobilePhoneAuthenticationToken authRequest = new MobilePhoneAuthenticationToken(username);
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);

    }

    private String obtainUsername(HttpServletRequest request) {

        Object username = request.getAttribute(mobilePhoneAttribute);
        if (username == null) {
            throw new IllegalStateException(
                    "Request has been verified, " +
                            "but username attribute: " + mobilePhoneAttribute + " not set after verification succeed"
            );
        }

        return username.toString();
    }

    protected void setDetails(HttpServletRequest request, MobilePhoneAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }



    // ~ Getter Setter
    // =================================================================================================================

    public String getMobilePhoneAttribute() {
        return mobilePhoneAttribute;
    }

    public void setMobilePhoneAttribute(String mobilePhoneAttribute) {
        if (StringUtils.isBlank(mobilePhoneAttribute)) {
            throw new IllegalArgumentException("mobilePhoneAttribute can not be blank");
        }
        this.mobilePhoneAttribute = mobilePhoneAttribute;
    }
}
